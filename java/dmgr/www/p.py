from bs4 import BeautifulSoup
from bs4.element import Comment
import urllib.request
import re
import sys
import io

def tag_visible(element):
    if element.parent.name in ['style', 'script', 'head', 'title', 'meta', '[document]']:
        return False
    if isinstance(element, Comment):
        return False
    return True


def text_from_html(body):
    soup = BeautifulSoup(body, 'html.parser')
    texts = soup.findAll(text=True)
    visible_texts = filter(tag_visible, texts)  
    return u" ".join(t.strip() for t in visible_texts)


baseurl = sys.argv[1]
queue = {sys.argv[2]}
docid = 0
visited = {""}

while len(queue) != 0:

    url = queue.pop()
    if url in visited:
        continue

    url = url.replace("&amp;","&")
    if ( url[0] == '/' ):
        url = baseurl + url
    
    visited.add(url)
    print( '>' + url )
    try:
        html_code = urllib.request.urlopen(url).read().decode('utf-8')
        docid = docid + 1
    except:
        print('error occured downloading : ' + url)
        continue
        
    soup = BeautifulSoup(html_code)
    links = soup.find_all('a')
    for tag in links:
        link = tag.get('href',None)
        if link is not None and link[0] == '/' and '.' not in link:
            queue.add(link)            

    with io.open('C:\\Users\\mpataki\\Documents\\se\\java\\dmgr\\tmp\\' + str(docid), "w", encoding="utf-8") as f:
        f.write(text_from_html(html_code))
    
    with io.open('C:\\Users\\mpataki\\Documents\\se\\java\\dmgr\\www\\textcontent\\' + str(docid), "w", encoding="utf-8") as f:
        f.write(html_code)
