from bs4 import BeautifulSoup
from nltk import FreqDist
from nltk.tokenize import word_tokenize


def html_tag_remove(html):
    """Gets and clean tags like scripts, style, meta and noscript from the html page

    Parameters
    ----------
    html : str
        html string
    Returns
    -------
    str
        string only without tags
    """
    html_parse = BeautifulSoup(html, "html.parser")  # build html tree
    for tag in html_parse(['script', 'style', 'meta', 'noscript']):
        # remove tags from the html tree
        tag.decompose()
    return ' '.join(html_parse.stripped_strings)


def getwords_count(text):
    """Get text and apply NLTK functions to calc word frequency distribution

    Parameters
    ----------
    text :str
        text from url cleaned up
    Returns
    -------
    dict
        dictionary with a top 100 words and frequency
    """
    if text is None:
        raise NotImplementedError("Text is empty!")

    word_token = word_tokenize(text)
    words = [word for word in word_token if (word.isalpha() and len(word) > 1)]
    fdist = FreqDist(words)
    dict_words_count = {}
    words_ordered_desc = sorted(fdist, key=fdist.get, reverse=True)
    for word in words_ordered_desc[:100]:  # Get top 100 words
        dict_words_count[word] = fdist[word]
    return dict_words_count
