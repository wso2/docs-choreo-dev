from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText
from email.utils import formataddr
import smtplib
import ssl
import os

smtp_username = os.environ.get("SMTP_USERNAME")
smtp_password = os.environ.get("SMTP_PASSWORD")
smtp_server = os.environ.get("SMTP_SERVER", "email-smtp.us-east-1.amazonaws.com")
smtp_port = int(os.environ.get("SMTP_PORT", 587))
sender_name = os.environ.get("SENDER_NAME", "Choreo Platform Engineering Team")
sender_email = os.environ.get("SENDER_EMAIL", "choreo-automation-bot@wso2.com")
recipient_email = os.environ.get("RECIPIENT_EMAIL")
cc_recipient_email = os.environ.get("CC_RECIPIENT_EMAIL")
email_subject = os.environ.get("EMAIL_SUBJECT")
html_template_file = os.environ.get("HTML_TEMPLATE_FILE")

context = ssl.SSLContext(ssl.PROTOCOL_TLS)
connection = smtplib.SMTP(smtp_server, port=smtp_port)
connection.starttls(context=context)

connection.login(smtp_username, smtp_password)

with open(html_template_file) as f:
    msg = f.read()

message = MIMEMultipart()

message["From"] = formataddr((sender_name, sender_email))
message["To"] = recipient_email
message["Cc"] = cc_recipient_email
message["Subject"] = email_subject
message.attach(MIMEText(msg, "html"))
connection.sendmail(message["From"], message["To"], message.as_string())
