from webapp import db

#table Word_ranking defined by class
class Word_ranking(db.Model):
    id = db.Column(db.Integer, primary_key=True)  # hash value from word
    word = db.Column(db.String(200), unique=False, nullable=False)
    freqcount = db.Column(db.Integer, nullable=False)  # Total of word frequency in text from the urls

    def __init__(self, code, word, freq):
        self.id = code
        self.word = word
        self.freqcount = freq
