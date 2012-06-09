import cgi
import os
from google.appengine.ext.webapp import template
from google.appengine.ext import webapp
from google.appengine.ext.webapp.util import run_wsgi_app
from google.appengine.api import users

class MainClass(webapp.RequestHandler):
    def get(self):
        user = users.get_current_user()
        if user:
                self.response.headers['Content-Type'] = 'text/plain'
                self.response.out.write('Hello, '+ user.nickname()  +', Welcome to my world of languages!!')
        else:
                self.redirect(users.create_login_url(self.request.uri))

application = webapp.WSGIApplication([('/',MainClass)],debug = True)

def main():
    run_wsgi_app(application)

if __name__ == "__main__":
    main()
