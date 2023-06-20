# flaskwebapp using NLP (NLTK toolkit) to rank words from text
1) Create a Python web application using Flask web server ( Write clean, well documented code ).
2) The project should have a single page with a form where I can enter a URL to any website (e.g. Wikipedia or SAPONews)
3) The application should fetch that url and build a dictionary that contains the frequency of use of each word on that page.  4) Use this dictionary to display, on the client’s browser, a “word ranking” of the top 100 words.
5) Each time a URL is fetched, it should save the top 100 words to a MySQL DB with the following three columns and redirect to the admin page:
	a) The primary key for the word is the hash of the word.
	b) The word itself.
	c) The total frequency count of the word.
	Each time a new URL is fetched, you should INSERT or UPDATE the word count.

6) An “admin” page, that will list all words entered into the DB, ordered by frequency of usage ( biggest to lower ).
