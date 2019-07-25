#!/usr/bin/python3.5
import datetime
import time
from flask import Flask, flash, redirect, render_template, request, session, abort, json, jsonify, url_for
from flask_restful import Resource, Api, reqparse
import os
from selenium.common.exceptions import TimeoutException, NoSuchElementException
from selenium.webdriver import DesiredCapabilities
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.wait import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from bs4 import BeautifulSoup
import base64
import hashlib
from Crypto import Random
from Crypto.Cipher import AES
import cropImage

app = Flask(__name__)
api = Api(app)
key = "16bit key"
userLog = False

class AESCipher(object):
    def __init__(self, key):
        self.key = hashlib.sha256(key.encode()).digest()

    def encrypt(self, raw):
        raw = self._pad(raw)
        iv = Random.new().read(AES.block_size)
        cipher = AES.new(self.key, AES.MODE_CBC, iv)
        return base64.b64encode(iv + cipher.encrypt(raw))

    def decrypt(self, enc):
        enc = base64.b64decode(enc)
        iv = enc[:AES.block_size]
        cipher = AES.new(self.key, AES.MODE_CBC, iv)
        return self._unpad(cipher.decrypt(enc[AES.block_size:])).decode('utf-8')

    def _pad(self, s):
        return s + (AES.block_size - len(s) % AES.block_size) * chr(AES.block_size - len(s) % AES.block_size)

    @staticmethod
    def _unpad(s):
        return s[:-ord(s[len(s) - 1:])]

cipher = AESCipher(key=key)

class fenixWebscrap(object):
    def __init__(self):
        self.dcap = dict(DesiredCapabilities.FIREFOX)
        self.dcap['phantomjs.page.settings.userAgent'] = ('Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/73.0.3683.75 Chrome/73.0.3683.75 Safari/537.36')
        self.driver = webdriver.PhantomJS(executable_path='/opt/phantomjs/bin/phantomjs', desired_capabilities=DesiredCapabilities.FIREFOX)
        self.driver.set_window_size(200, 100)
        self.HEADERS, self.msgLstJSON, self.msgdetailJSON = None, None, None
        self.comunLink, self.estudanteLink, self.pessoalLink = None, None, None
        self.userlog = False

    def _getMsgDetails(self,driver,nTotal,msgLstJSON):
        for rownum in range(nTotal):
            driver.implicitly_wait(5)
            msgbody = driver.find_element_by_css_selector('.communication-message-inbox-as-mail-box > div:nth-child(3) > div:nth-child(1) > div:nth-child(7)')
            nextBtn = driver.find_element_by_css_selector('.communication-message-inbox-as-mail-box > div:nth-child(3) > div:nth-child(1) > div:nth-child(1) > div:nth-child(1) > div:nth-child(9) > div:nth-child(1)')
            sMsg = msgbody.get_attribute('innerHTML')
            msgFrom = driver.find_element_by_css_selector('div.v-align-center > div:nth-child(1)').text
            msgLstJSON[rownum].update({'sMsg': sMsg,'sFrom':msgFrom})
            driver.execute_script("arguments[0].click();", nextBtn)
            time.sleep(1)
        return msgLstJSON

    def _getMsg(self,rows):
        msgLst = []
        for rownum in range(len(rows)):
            row = rows[rownum]
            col = row.find_elements(By.TAG_NAME, "td")
            sFrom = col[0].text
            sSubject = col[1].text
            sDate = col[2].find_element(By.TAG_NAME, "input").get_attribute("value")
            sMsg = ''
            msgLine = {'sFrom': sFrom, 'sSubject': sSubject, 'sDate': sDate, 'sMsg': sMsg}
            msgLst.append(msgLine)
        return msgLst

    def login(self,user,password):
        urlpage = 'https://fenix.iscte-iul.pt/loginPage.jsp'
        try:
            password = str(cipher.decrypt(password))
        except:
            password = password
        data = {'username': user, '_request_checksum_': '', 'ok': 'Entrar','password': password}
        WebDriverWait(self.driver, 5).until(EC.presence_of_all_elements_located)
        self.driver.get(urlpage)
        self.driver.find_element_by_name("username").send_keys(data['username'])
        self.driver.find_element_by_name("password").send_keys(data['password'])
        login_button = self.driver.find_element_by_name("ok")
        login_button.click()
        try:
            user = self.driver.find_element_by_id("user").text
            self.userLog = True
            soup = BeautifulSoup(self.driver.page_source, 'lxml')
            self.comunLink = soup.findAll("a", text='Comunicação')[0]
            self.pessoalLink = soup.findAll("a", text='Pessoal')[0]
            self.estudanteLink = soup.findAll("a", text='Estudante')[0]
            cookie_tmp = []
            for cookie in self.driver.get_cookies():
                data = "{}={}".format(cookie['name'], cookie['value'])
                cookie_tmp.append(data)
            _cookie = ';'.join(cookie_tmp)

            self.HEADERS = {
                "User-Agent": "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:51.0) Gecko/20100101 Firefox/51.0",
                "Accept": "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
                "Accept-Language": "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3",
                "Accept-Encoding": "gzip, deflate, br",
                "Connection": "keep-alive",
                "Host": "fenix.iscte-iul.pt",
                "Cookie": _cookie,
                "Upgrade - Insecure - Requests": "1"
            }
            msg = "OK"
        except TimeoutException:
            msg = "ERROR! TIMEOUT"
            self.userLog = False
        except NoSuchElementException:
            self.userLog = False
            msg = "ERROR!"
        return msg

    def logout(self):
        self.driver.delete_all_cookies()
        self.driver.close()

    def getMessage(self):
        if not self.userLog:
            return json.dumps({'status':'ERROR! NO LOGIN USER'})
        try:
            self.driver.get('https://fenix.iscte-iul.pt' + self.comunLink['href'])
            table_id = self.driver.find_element(By.CLASS_NAME, 'v-table-table')
            rows = table_id.find_elements(By.TAG_NAME, "tr")
            msgLstJSON = self._getMsg(rows)
            mbutton = rows[0].find_elements(By.TAG_NAME, "td")[1].find_element(By.CLASS_NAME, 'v-button')
            self.driver.execute_script("arguments[0].click();", mbutton)
            self.driver.implicitly_wait(5)
            msgdetailJSON = self._getMsgDetails(self.driver, len(rows), msgLstJSON)
        except TimeoutException:
            return 'TIMEOUT'
        except NoSuchElementException:
            return 'NOTFOUND'
        return msgdetailJSON

    def getInfo(self):
            if not self.userLog:
                return json.dumps({'status':'ERROR! NO LOGIN USER'})
            infoDetails = []
            try:
                self.driver.get('https://fenix.iscte-iul.pt' + self.estudanteLink['href'])
                soup_level2 = BeautifulSoup(self.driver.page_source, 'lxml')
                cvEtudanteLink = soup_level2.findAll("a", text='Currículo do estudante')[0]
                url = "https://fenix.iscte-iul.pt" + cvEtudanteLink['href']
                self.driver.get(url)
                cvEstud = self.driver.find_element_by_css_selector(".showpersonid").text
                dataReg = self.driver.find_element_by_css_selector(".tstyle1 > tbody:nth-child(1) > tr:nth-child(1) > td:nth-child(2)").text
                numEstud = self.driver.find_element_by_css_selector(".tstyle1 > tbody:nth-child(1) > tr:nth-child(2) > td:nth-child(2)").text
                curso = self.driver.find_element_by_css_selector(".tstyle1 > tbody:nth-child(1) > tr:nth-child(3) > td:nth-child(2)").text
                rows = self.driver.find_elements(By.CSS_SELECTOR, "tr.scplanenrollment")
                avalLst = []
                for rownum in range(len(rows)):
                    row = rows[rownum]
                    uc = row.find_element(By.CSS_SELECTOR, ".scplancolcurricularcourse").text
                    grade = row.find_element(By.CSS_SELECTOR, ".scplancolgrade").text
                    score = {'class': uc, 'grade': grade}
                    avalLst.append(score)
                imgLink = self.driver.find_element_by_css_selector(".showphoto")
                dnimg = imgLink.get_attribute("src")
                self.driver.get(dnimg)
                avatar = cropImage.cropImage(self.driver.get_screenshot_as_base64())
                infoDetails = [{'avatar':avatar,'cvEstud':cvEstud.split('-')[1],'dataMatricula':dataReg,'numEstud': numEstud, 'curso':curso, 'avaliacao':avalLst}]
            except TimeoutException:
                return 'TIMEOUT'
            except NoSuchElementException:
                return 'NOTFOUND'
            return infoDetails

    def getCal(self):
        if not self.userLog:
            return json.dumps({'status':'ERROR! NO LOGIN USER'})
        try:
            infocal = []
            self.driver.get('https://fenix.iscte-iul.pt' + self.estudanteLink['href'])
            soup_level2 = BeautifulSoup(self.driver.page_source, 'lxml')
            cvAvalLink = soup_level2.findAll("a", text='Avaliações')[0]
            url = "https://fenix.iscte-iul.pt" + cvAvalLink['href']
            WebDriverWait(self.driver, 5).until(EC.presence_of_all_elements_located)
            self.driver.get(url)
            time.sleep(5)
            calLink = self.driver.find_element(By.CSS_SELECTOR, 'div#gwt-uid-8.v-caption')
            calLink.click()
            time.sleep(5)
            table_id = self.driver.find_element(By.CLASS_NAME, 'v-calendar-month')
            rows = table_id.find_elements(By.TAG_NAME, "tr")
            dictMes = {'jan':1,'fev':2,'mar':3,'abr':4,'mai':5,'jun':6,'jul':7,'ago':8,'set':9,'out':10,'nov':11,'dez':12,}
            todayDate = datetime.datetime.today().date()
            for rownum in range(len(rows)):
                row = rows[rownum]
                cols = row.find_elements(By.TAG_NAME, "td")
                for col in cols:
                    day = col.find_element_by_class_name("v-calendar-day-number").text
                    weeknumber = self.driver.find_element(By.CLASS_NAME, 'v-calendar-week-numbers').find_elements_by_tag_name("tr")[rownum].text
                    if (int(weeknumber) >= todayDate.isocalendar()[1]):
                        mesToday = str(todayDate.month)
                        if len(col.text) < 3:
                            sdate = str(todayDate.year) + "-" + mesToday + "-" + day
                            date = datetime.datetime.strptime(sdate, "%Y-%m-%d")
                        elif len(col.text) > 2 and len(col.text) < 8:
                            mesToday = dictMes[str(col.text)[-3:]]
                            if (str(col.text)[-3:] == 'jan'):
                                sdate = str(todayDate.year + 1) + "-" + mesToday + "-" + day
                                date = datetime.datetime.strptime(sdate, "%Y-%m-%d")
                            else:
                                sdate = str(todayDate.year) + "-" + mesToday + "-" + day
                                date = datetime.datetime.strptime(sdate, "%Y-%m-%d")
                        else:
                            sdate = str(todayDate.year) + "-" + mesToday + "-" + day
                            date = datetime.datetime.strptime(sdate, "%Y-%m-%d")
                        if len(col.text) > 8:
                            for event in col.find_elements_by_class_name("v-calendar-event-month"):
                                hour = str(event.text)[0:5]
                                event = str(event.text)[5:].strip()
                                infocal.append({'weeknumber':weeknumber,'dia':day,'hour':hour, 'data':str(date.date()),'evento':event})
        except TimeoutException:
            return 'TIMEOUT'
        except NoSuchElementException:
            return 'NOTFOUND'
        return infocal

    def getPerson(self,person):
            if not self.userLog:
                return json.dumps({'status':'ERROR! NO LOGIN USER'})
            try:
                avatarNotFound = "iVBORw0KGgoAAAANSUhEUgAAAt" #default avatar no picture
                self.driver.get('https://fenix.iscte-iul.pt' + self.pessoalLink['href'])
                soup_level2 = BeautifulSoup(self.driver.page_source, 'lxml')
                procurarLink = soup_level2.findAll("a", text='Procurar Pessoa')[0]
                url = "https://fenix.iscte-iul.pt" + procurarLink['href']
                self.driver.get(url)
                inputSearch = self.driver.find_element_by_css_selector("table.search:nth-child(8) > tbody:nth-child(1) > tr:nth-child(1) > td:nth-child(2) > input:nth-child(1)")
                inputSearch.send_keys(person)
                inputPhoto = self.driver.find_element_by_css_selector("table.search:nth-child(8) > tbody:nth-child(1) > tr:nth-child(2) > td:nth-child(2) > input:nth-child(1)")
                inputPhoto.click()
                login_button = self.driver.find_element_by_css_selector("input.inputbutton:nth-child(4)")
                login_button.click()
                soup_search = BeautifulSoup(self.driver.page_source, 'lxml')
                divresult = soup_search.findAll('div', attrs={'class': 'pp'})
                personList = []
                for idx in divresult:
                    table_id = idx.find("table", {"class": "ppid"})
                    col = table_id.find("tr").find("td").text
                    name = str(col).split("(")[0].strip()
                    email = str(col).split("(")[1].split(")")[0].strip()
                    type = str(col).split(")")[1].strip()
                    imgLink = idx.find("img")
                    self.driver.implicitly_wait(2)
                    self.driver.get("https://fenix.iscte-iul.pt" + imgLink['src'])
                    avatarEncode = self.driver.get_screenshot_as_base64()
                    if str(avatarEncode)[0:26] == avatarNotFound:
                        avatarEncode = "R0lGODlhZABkAOYAAMnJyfDw8N/f39TU1MfHx7+/v6CgoOLi4tfX18jIyPf398bGxoiIiJiYmNDQ0LCwsIGBgaioqNvb2+np6bi4uObm5sHBwb29vZCQkN7e3p2dnbGxsfj4+PT09K2trc/Pz7W1tbe3t4mJiczMzJSUlIKCgoWFhefn5/v7+5mZmaOjo6mpqYODg8PDw+3t7aWlpcTExLm5uZGRkZWVlcLCwsvLy7S0tIaGhry8vI2NjbKysoSEhLq6uoqKipycnKKioqGhoaSkpI+Pj5+fn5aWlqurq/Hx8aysrO/v74yMjOPj446Ojru7u4eHh8DAwJKSkqampqenp6+vr+Dg4Nzc3IuLi9XV1a6urqqqqvz8/J6enpeXl+zs7Orq6s7OzvPz85ubm+Xl5b6+vuTk5O7u7tPT0/Ly8v///4CAgMXFxQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACH5BAAAAAAALAAAAABkAGQAAAf/gGmCg4SFhoeIiYqLjI2Oj5CRkpOUlZaXmJmam5ydjQueoaKCCQAAMBYwpQAEo66VAGkgQURJN0kkQw8XaQAJr8CMpTEkaMbHxyVLBiG9wc+EpRQpEMjW1jIgrNDBCdPV1+HIGk6x3KMAL+Di7MYMRwS/550ARe33yCQ45vOZABvr8N0z8YBfP1ghdghcaEwFAVAHJxG40IShRQ0GIz4C4MOixwgZNS5aYMGER4sQbIQUiajeSY9LILJcRODJS486Vs4cRPLGTYskWu1sueEnyhhChxYCsMWoRQ86Z05U6HQhkKgsE4SoynCGPKWDtHJdmAPs0qJj8YmgIRMsAChp//GZsNBWKYAfce+VuJDUrYq87SAgNSsIwF/A7EB89YuY3QasGu82FgeVcK8Xk8OtgBzRZWZrGhbbvfLZmogWdXcCkFIamVfLAGy0PvaC88EEIGYbq0zYsG40iodKODBgUI7fj4cGOHMgzYICLH5jVM48zcQSv4UoXd6cCpkHBQQQYBBBAAIDxhqEF0CBwbEICM4LEPAegYAC7tHMbxDfRo0JARxgmyvcpaHEGQqcoSASCiqIARoEKCBAgvSh8UCDDRpDwBlInHDGCcYomKCCHDQ4gUgFHoAgAwUoaAADCVKAhnvkOYhGjGi0eAYaGKyIBgJnRICGgvglaEQaKqKAYv91Ko6BhgEKGiPAGQWg0YCHDRoA5Y5PRkkBgvMxWKWLaExxhhJpDKDgks2pOEGXXE5ZJJVbahklnDmCOZ8ADwx5BnphVKfmGWwieUYAMmwpJZ0KMtAAmQrKuOGOjyqA3owh/okGF4KuqVGKZxiBQxB3ygljgwmipyOGxnyAIZcu+mBEp4RqVEEAEqQhQQAVAGDBFBUSwKeV9kUgbAPGPCDABxdyaaF97EkpwAYJ3JqrAwEE0JsHAbWDwYMMAImAXgWIZplzafSw0JcYItvOaamdm8AMCxkgrHgP3iNDvOcCAERjKQyomgeNbXbuIQTw0C1Xa/HbLxiAgTTPASc6Mqj/IBRPZFJaPbAlCIAOPHKxJAqG3MjIJacT11WCqNmcyJ5GgkCuFns6s3UFUMVVFOYgkEEkI0eSbci3DgCggINkEIALGXg69AIw8CCekMo+oOx5x2AgLAJ9aiiAAR8IMMAIggytdMW7Vqw0ri3HDImCxS2HQoMVlP1qrXAT8MWHCsjY4ogKCtkA4Gd8sCjgJwgCN5BnkL1c3R2c0UGJdQf99hlxH2qokg6UnMYEnsJtRaOY/o0skCCKa6WCyE5JX4tZKI55GiVKAICCI0igOQAlAmD5I3CnUeDFKnbQNt6Y12CGgh+412KFipKpH5XTV7ml7MVVcIYLQBrvwhkcZDv3/wC/OxL88GuqqG2aoWNOgBhOUJgn9FFKL2f1cGKfRucoaJ/rcuHLVgAcUL5GnI9W7DtDyECHvAEQoAYQiECUTPejM4xLdY86Q+uod700BI924DtDLJrWAbI5o4CMOGBzRhY5Bc2tgYxTEAHy9CpkDQ5DhsNfBz/YtO0VpoUK0hYKFzG0NFhrf9kqzAECMIERJFF4AShDGrwwHxnNLzwfcBePttY1CA2rAQLoQiyKmAYnBqA4hVkb95C4Pm4Q4Ag+scbzfsIAC/TlYL3Qgjjm+JMgCEwjC6jBEdihrBnSsQV/nAcBANCCCGAnMym4wDaUssgEFGAF6moNCzSwjzvOY9cBpkhDDKJAgo39ZpOdVKQpLKCDIeRgYb9BwyaZEMpXLKAUaWACFogQx1i2owQNsEEvHFaJRQKABiD4gRBg6ct25GAFFvBFJkoxEQ+kQATNNEoThhADXEZiAcaEQQheIIPoZJMrEJjBBmAwSUWAkpE88IAPqnDOxiQBCgWQZiIssIIZiICZ9YyLCTQQgkUiIj8B/c0TpOCMQiQ0m0LYACgJ8dBzykAHBk1DRetJAsUkYKMBJcJWQErSkpr0pChNqUpXytKWuvSlMI2pTGdK05ra9KY4rUogAAA7"
                    avatar = cropImage.cropImage(avatarEncode)
                    personList.append({'name':str(name).strip(),'email':str(email).strip(),'type':type,'avatar':avatar})
            except TimeoutException:
                return 'TIMEOUT'
            except NoSuchElementException:
                return 'NOTFOUND'
            return personList

@app.route('/login', methods=['POST'])
def login():
    if request.method != 'POST':
        return jsonify({'status': 'ERROR! NO LOGIN USER'})
    if (not request.json) or (not 'username' in request.json) or (not 'password' in request.json):
        abort(400)
    fenixSession = fenixWebscrap()
    status = fenixSession.login(request.json['username'],request.json['password'])
    if status == 'OK':
        return jsonify({'status': status})
    else:
        return jsonify({'status': status})
        
@app.route('/info', methods=['POST'])
def info():
    if request.method != 'POST':
        return jsonify({'status': 'ERROR! NO LOGIN USER'})
    if (not request.json) or (not 'username' in request.json) or (not 'password' in request.json):
        abort(400)
    fenixSession = fenixWebscrap()
    status = fenixSession.login(request.json['username'],request.json['password'])
    if status == 'OK':
        infoDetails = fenixSession.getInfo()
        fenixSession.logout()
        if infoDetails in ['TIMEOUT', 'NOTFOUND']:
            status = 'ERROR! TIMEOUT or ELEMENT NOT FOUND'
            infoDetails = []
        return jsonify({'status': status, 'info': infoDetails})
    else:
        return jsonify({'status': status})

@app.route('/message', methods=['POST'])
def message():
    if request.method != 'POST':
        return jsonify({'status': 'ERROR! NO LOGIN USER'})
    if (not request.json) or (not 'username' in request.json) or (not 'password' in request.json):
        abort(400)
    fenixSession = fenixWebscrap()
    status = fenixSession.login(request.json['username'], request.json['password'])
    if status == 'OK':
        msgList = fenixSession.getMessage()
        fenixSession.logout()
        if msgList in ['TIMEOUT', 'NOTFOUND']:
            status = 'ERROR! TIMEOUT or ELEMENT NOT FOUND'
            msgList = []
        return jsonify({'status': status, 'message': msgList})
    else:
        return jsonify({'status': status})

@app.route("/person", methods=['POST'])
def person():
    if request.method != 'POST':
        return jsonify({'status': 'ERROR! NO LOGIN USER'})
    if (not request.json) or (not 'username' in request.json) or (not 'password' in request.json) or (not 'search' in request.json):
        abort(400)
    fenixSession = fenixWebscrap()
    status = fenixSession.login(request.json['username'], request.json['password'])
    if status == 'OK':
        personList = fenixSession.getPerson(request.json['search'])
        fenixSession.logout()
        if personList in ['TIMEOUT', 'NOTFOUND']:
            status = 'ERROR! TIMEOUT or ELEMENT NOT FOUND'
            personList = []
        return jsonify({'status': status, 'person': personList})
    else:
        return  jsonify({'status': status})

@app.route("/calendar", methods=['POST'])
def calendar():
    if request.method != 'POST':
        return jsonify({'status': 'ERROR! NO LOGIN USER'})
    if (not request.json) or (not 'username' in request.json) or (not 'password' in request.json):
        abort(400)
    fenixSession = fenixWebscrap()
    status = fenixSession.login(request.json['username'], request.json['password'])
    if status == 'OK':
        calList = fenixSession.getCal()
        fenixSession.logout()
        if calList in ['TIMEOUT', 'NOTFOUND']:
            status = 'ERROR! TIMEOUT or ELEMENT NOT FOUND'
            calList = []
        return jsonify({'status': status, 'calendar': calList})
    else:
        return  jsonify({'status': status})

@app.route("/syncALL", methods=['POST'])
def syncAll():
    if request.method != 'POST':
        return jsonify({'status': 'ERROR! NO LOGIN USER'})
    if (not request.json) or (not 'username' in request.json) or (not 'password' in request.json):
        abort(400)
    fenixSession = fenixWebscrap()
    status = fenixSession.login(request.json['username'], request.json['password'])
    if status == 'OK':
        getInfo = fenixSession.getInfo()
        getMessage = fenixSession.getMessage()
        getCal = fenixSession.getCal()
        fenixSession.logout()
        return jsonify({'status': status, 'calendar': getCal, 'message':getMessage,'info':getInfo})
    else:
        return  jsonify({'status': status})
