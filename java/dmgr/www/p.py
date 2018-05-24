from bs4 import BeautifulSoup
from bs4.element import Comment
import urllib.request
import re
import sys
import io
import socket

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

lf = open("linktab.txt", "w", encoding="utf-8", buffering=1)


print("usage : p.py <seed> <base> <depth> <report-host> <report-port>")

if len(sys.argv) < 5:
    exit(0)

baseurl = sys.argv[1]
queue = {(sys.argv[2], 0)}
maxdepth = int(sys.argv[3])
reporthost = sys.argv[4]
reportport = int(sys.argv[5])
docid = int(sys.argv[6])
visited = {""}

while len(queue) != 0:

    urlt = queue.pop()
    url = urlt[0]
    dpt = urlt[1]
    if dpt > maxdepth:
        continue

    if url in visited:
        continue

    url = url.replace("&amp;","&")
    if ( url[0] == '/' ):
        url = baseurl + url

    visited.add(url)
    print('>' + str(urlt))
    try:
        html_code = urllib.request.urlopen(url).read().decode('utf-8')
        docid = docid + 1
        report = str(docid) + '\t' + url + '\n'
        lf.write(report)
        reporter = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        reporter.connect((reporthost, reportport))
        reporter.sendall(report.encode('utf-8'))
        reporter.close()
    except:
        print('error occured downloading : ' + url)
        continue
    
    soup = BeautifulSoup(html_code)
    links = soup.find_all('a')
    for tag in links:
        link = tag.get('href',None)
        if link is not None and link[0] == '/' and '.' not in link:
            queue.add((link, dpt + 1))
            
    with io.open('C:\\Users\\mpataki\\Documents\\se\\java\\dmgr\\tmp\\' + str(docid), "w", encoding="utf-8") as f:
        f.write(text_from_html(html_code))
    
    with io.open('C:\\Users\\mpataki\\Documents\\se\\java\\dmgr\\www\\textcontent\\' + str(docid), "w", encoding="utf-8") as f:
        f.write(html_code)
