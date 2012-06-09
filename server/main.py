import cgi
import os
import urllib
from google.appengine.ext import blobstore
from google.appengine.ext.webapp import template
from google.appengine.ext import webapp
from google.appengine.ext import db
from google.appengine.ext.webapp import blobstore_handlers
from google.appengine.ext.webapp.util import run_wsgi_app
from google.appengine.api import users

class UserDataBase(db.Model):
    username = db.StringProperty(required = True)
    video = blobstore.BlobReferenceProperty(required = True)
    #contacts = db.StringProperty(multiline = True)
    #location = db.StringProperty()
    #data = db.DateTimeProperty()
    #time = db.StringProperty()

class UploadHandler(blobstore_handlers.BlobstoreUploadHandler):
    def post(self):
        upload_files = self.get_uploads('file')
        self.response.out.write('Hello, Welcome to Bachchao WebSite.' + upload_files[0].filename)
        if not users.get_current_user():
            upload_files.delete()
        file_info = UserDataBase(username = users.get_current_user().nickname(), video = upload_files[0].key())
        db.put(file_info)
        self.redirect('/')

class MainClass(webapp.RequestHandler):
    def get(self):
        user = users.get_current_user()
        self.response.headers['Content-Type'] = 'text/html'
        self.response.out.write('<html><body>')
        if user:
                self.response.out.write('Hello, '+ user.nickname()  +', Welcome to Bachchao WebSite.')
                upload_url = blobstore.create_upload_url('/upload')
                self.response.out.write('<html><body>')
                self.response.out.write('<form action="%s" method="POST" enctype="multipart/form-data">' % upload_url)
                self.response.out.write("""Upload File: <input type="file" name="file"><br> <input type="submit" name="submit" value="Submit"> </form></body></html>""")
                blobs = db.GqlQuery("SELECT * "
                                "FROM UserDataBase "
                                "WHERE username = :1 ",user.nickname())
                for b in blobs:
                    self.response.out.write('<li><a href="/serve/%s' % str(b.video.key()) + '">' + str(b.video.filename) + '</a>')
        else:
            self.redirect(users.create_login_url(self.request.uri))
        self.response.out.write('</body></html>')
        
class ServeHandler(blobstore_handlers.BlobstoreDownloadHandler):
    def get(self, blob_key):
        blob_key = str(urllib.unquote(blob_key))
        if not blobstore.get(blob_key):
            self.error(404)
        else:
            self.send_blob(blobstore.BlobInfo.get(blob_key), save_as=True)

application = webapp.WSGIApplication([('/',MainClass),('/upload', UploadHandler),('/serve/([^/]+)?',ServeHandler)],debug = True)

def main():
    run_wsgi_app(application)

if __name__ == "__main__":
    main()
