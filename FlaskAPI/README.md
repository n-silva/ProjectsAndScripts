Project is about a Flask Api, using Selenium driver with PhantonJS to webscrap website and BeautifulSOAP to mining data from the website. 
The API call be call from any language using a AESCipher 16Bit key for encryption, and on this sample I'm using a java/android App to scrap info from fenix and return data to a mobile app.


Steps for Ubuntu Server (Systemd):

        - made the necessary changes to FlaskAPI.service file
        - copy FlaskAPI.service into /etc/systemd/system/
        - run: systemctl daemon-reload && systemctl enable fenixapi && systemctl start fenixapi --no-block

Now the Api enbale and is running as service with auto-start on boot

For java AESCipher implementation follow this sample in you java project:

	String key = '16bit key';
	Ciphers c = new Ciphers(key);
	password = c.encrypt(password).toString().trim();
