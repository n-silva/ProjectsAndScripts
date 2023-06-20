import requests
from flask import render_template, request,flash, redirect, url_for
from webapp import app, db
from webapp.models import Word_ranking
from webapp.tools import html_tag_remove, getwords_count

@app.route('/', methods=['GET','POST'])
def home():
    if request.method == 'POST':
        url = request.form.get('url')
        session = requests.Session()
        countWordsDict = None
        errorMessage = None
        try:
            page = session.get('http://' + url) if (requests.utils.urlparse(url).scheme == '') else session.get(url)
            countWordsDict = getwords_count(html_tag_remove(page.content).upper())
            for word in countWordsDict:
                wordranking = Word_ranking(abs(hash(word)) % (10 ** 8), word, countWordsDict[word])
                wordExist = Word_ranking.query.filter_by(word=word).first()
                if wordExist:
                    wordExist.freqcount += countWordsDict[word]
                    db.session.commit()
                else:
                    db.session.add(wordranking)
                    db.session.commit()
        except requests.exceptions.Timeout:
            errorMessage = 'Timeout, url cannot be load, try again'
        except requests.exceptions.TooManyRedirects:
            errorMessage = 'URL load loop, try a different one'
        except requests.exceptions.RequestException as e:
            errorMessage = 'Url cannot be loaded.'

        return render_template('index.html',   wordsdict=countWordsDict, error=errorMessage)
        #return redirect('/admin')  # Auto redicret to admin page

    return render_template('index.html')


@app.route('/admin' , methods=['GET'], defaults={"page": 1})  # Set initial page number
@app.route('/admin/<int:page>', methods=['GET'])
def admin(page):
    page = page  #current page
    per_page = 20  #Step registry by page
    wordranking = Word_ranking.query.order_by(Word_ranking.freqcount.desc()).paginate(page, per_page, error_out=False)  #query with pagination and order by desc
    return render_template('admin.html', wordsdict=wordranking)



