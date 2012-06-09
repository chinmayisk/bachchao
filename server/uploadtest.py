#BACHCHAO SERVER (Proof of Concept)
#The server program expects a GET request to'/pseudoupload' from the client. The client then receives an url as the response. The client can post multipart/form-data
#to the server with the filename contained within the attribute 'file', the results of the upload will be visible at the mainpage(http://bachchao.appspot.com)
import os
import urllib

from google.appengine.ext import blobstore
from google.appengine.ext import webapp
from google.appengine.ext.webapp import blobstore_handlers
from google.appengine.ext.webapp import template
from google.appengine.ext.webapp.util import run_wsgi_app
from django.utils import simplejson
    
#the handler to the default url http://bachchao.appspot.com    
class MainHandler(webapp.RequestHandler):
    def get(self):
        upload_url = blobstore.create_upload_url('/upload')
        self.response.out.write('<html><body>')
        self.response.out.write('<form action="%s" method="POST" enctype="multipart/form-data">' % upload_url)
        self.response.out.write("""Upload File: <input type="file" name="file"><br> <input type="submit" name="submit" value="Submit"> </form></body></html>""")
        i = 0
        #iterate through all the blobs(files) in the blobstore(a database of files) and print their names
        for b in blobstore.BlobInfo.all():
            self.response.out.write('<li><a href="/serve/%s' % str(b.key()) + '">' + str(b.filename) + '</a>')
            self.response.out.write('</li>')
            i = i+1
        self.response.out.write('<form action ="/delete" method = "POST"><input type ="submit" value="delete all"</form> ')

#a handler which handles uploads using GAE API and puts it into the blobstore(a database of files)
class UploadHandler(blobstore_handlers.BlobstoreUploadHandler):
    def post(self):
        self.get_uploads('file')
        self.redirect('/')

#the handler for downloads, this is called when the user clicks on a file listed in the mainpage
class ServeHandler(blobstore_handlers.BlobstoreDownloadHandler):
    def get(self, blob_key):
        blob_key = str(urllib.unquote(blob_key))
        if not blobstore.get(blob_key):
            self.error(404)
        else:
            self.send_blob(blobstore.BlobInfo.get(blob_key), save_as=True)

#the handler to return an url to client. The client can upload files to this url, with the name of the file in the attribute file
class pseudouploader(webapp.RequestHandler):
    def get(self):
        upload_url = blobstore.create_upload_url('/upload')
        self.response.headers['Content-Type'] = 'text/plain'
        self.response.out.write(upload_url)

#the handler called when delete button in the mainpage is clicked, it iterates through the list of all blobs in the blobstore(a databasr of files) and delted each one of them
class deleter(webapp.RequestHandler):
    def post(self):
        for b in blobstore.BlobInfo.all():
            b.delete()
        self.redirect('/')
        
#handler for '/data, it receives JSON data from a POST call from the client, containing lattitiude, longitude and list of contacts         
class DataHandler(webapp.RequestHandler):        
    def post(self):
        global database
        database = []
        lat = self.request.get('lat')
        lon = self.request.get('lon')
        contacts = self.request.get_all('contacts')
        contacts = JSONtoOBJECT(contacts)
        self.response.out.write(contacts)
        database.append([contacts,[lat,lon]])

#converts json objects to native python objects
def JSONtoOBJECT(contacts):
    temp = contacts
    return contacts

#the main function, the server calls this function upon start of the server
def main():
    application = webapp.WSGIApplication(
          [('/', MainHandler),
           ('/pseudoupload',pseudouploader),
           ('/upload', UploadHandler),
           ('/serve/([^/]+)?', ServeHandler),
           ('/delete',deleter),
           ('/data',DataHandler)
          ], debug=True)
    run_wsgi_app(application)

if __name__ == '__main__':
  main()