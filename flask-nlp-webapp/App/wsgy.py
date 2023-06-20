import os
from webapp import app
#generate app secret
app.secret_key = os.urandom(12)
#run app
app.run(debug=True, host='0.0.0.0', port=5000)
