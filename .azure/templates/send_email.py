from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText
from email.utils import formataddr
import smtplib
import ssl
import os
import logging

logging.basicConfig(level=logging.INFO)
required_env_variables = [
    "SMTP_USERNAME", "SMTP_PASSWORD", "RECIPIENT_EMAIL",
    "CC_RECIPIENT_EMAIL", "EMAIL_SUBJECT", "HTML_TEMPLATE_FILE"
]

missing_variables = [var for var in required_env_variables if os.environ.get(var) is None]
if missing_variables:
    raise EnvironmentError(f"Missing required environment variables: {', '.join(missing_variables)}")


def send_email():
    try:
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

        context = ssl.create_default_context()

        with smtplib.SMTP(smtp_server, port=smtp_port) as connection:
            connection.starttls(context=context)
            connection.login(smtp_username, smtp_password)

            with open(html_template_file) as template_file:
                msg = template_file.read()

            message = MIMEMultipart()
            message["From"] = formataddr((sender_name, sender_email))
            message["To"] = recipient_email
            message["Cc"] = cc_recipient_email
            message["Subject"] = email_subject
            message.attach(MIMEText(msg, "html"))

            senderrs = connection.sendmail(message["From"], message["To"].split(",") + message["Cc"].split(","),
                                           message.as_string())

            if senderrs:
                logging.error(f"Failed to send email. Errors: {senderrs}")
            else:
                logging.info("Email sent successfully.")
            connection.quit()
    except Exception as e:
        logging.error(f"An error occurred: {e}")


if __name__ == "__main__":
    send_email()
