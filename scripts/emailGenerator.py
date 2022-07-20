# -------------------------------------------------------------------------------------
#
# Copyright (c) 2022, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
#
# This software is the property of WSO2 Inc. and its suppliers, if any.
# Dissemination of any information or reproduction of any material contained
# herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
# You may not alter or remove any copyright or other notice from copies of this content.
#
# --------------------------------------------------------------------------------------

import mimetypes
from email.mime.base import MIMEBase
from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText
from email.mime.image import MIMEImage
from datetime import datetime
from email import encoders
import smtplib
import json
import matplotlib.pyplot as plt
import os

today = datetime.utcnow()

def sendEmail(emailBody, image_list, csv_file_list):
    sender = "no-reply@internal.choreo.dev"
    receiver = ["udhan@wso2.com", "chiranga@wso2.com"]

    email_user = os.environ.get('EMAIL_USER')
    email_password = os.environ.get('EMAIL_PASSWORD')

    msg = MIMEMultipart('alternative')
    msg['Subject'] = "[Choreo] Debug logs enabled components in Stage and Production " + datetime.strftime(today, '%Y-%m-%d')
    msg['From'] = sender

    msgText = MIMEText(emailBody, 'html')

    msg.attach(msgText)

    img_id = 1
    for i in image_list:
        fp = open(i, 'rb')
        msgImage1 = MIMEImage(fp.read())
        fp.close()
        msgImage1.add_header('Content-ID', '<image' + str(img_id) + '>')
        img_id += 1
        msg.attach(msgImage1)


    for csv_file in csv_file_list:
        ctype, encoding = mimetypes.guess_type(csv_file)
        if ctype is None or encoding is not None:
            ctype = "application/octet-stream"
        maintype, subtype = ctype.split("/", 1)
        fp = open(csv_file, "rb")
        attachment = MIMEBase(maintype, subtype)
        attachment.set_payload(fp.read())
        fp.close()
        encoders.encode_base64(attachment)
        attachment.add_header("Content-Disposition", "attachment", filename=csv_file)
        msg.attach(attachment)

    for i_receiver in receiver:
        msg['To'] = i_receiver

        s = smtplib.SMTP('email-smtp.us-east-2.amazonaws.com', 587)
        s.ehlo()
        s.starttls()
        s.login(email_user, email_password)
        s.sendmail(sender, i_receiver, msg.as_string())
        s.quit()


def emailBodyGenerate():
    
    emailBody = '<!DOCTYPE html>\
                <html>\
                <head>\
                <style>\
                    table {font-family: arial, sans-serif; font-size:110%; width: 50%;}\
                    td, th {border: 2px solid #dddddd; text-align: left; padding: 8px;}\
                    tr:hover {background-color: #dddddd;}\
                    tr:nth-child(even) {background-color: #dddddd;}\
                </style>\
                </head>\
                <body>\
                        <font size="2">Hi Team,</font><br><br>\
                        <br><br><font size="3">Purpose of this mail is to identify the components in which debug logs have been enabled in Stage and Production and disable debug logs in those. Following tables contain those components and their respective debug log counts.</font><br><br>\
                        <img src="cid:image1"><br><br>\
                        <img src="cid:image2"><br><br>\
                        <br><font size="3">To view the full lists view the attachments.</font>\
                        <br><i><font size="3">Note: This lists are created by filtering the logs using "debug"(case-insensitive) keyword, so there can be false positives.</font></i>\
                        <br><br><font size="3">Automated Email from SRE Team.</font>\
                </body>\
                </html>'

    return emailBody


def tabelCreator(dataSet, title, imageName):
    with open(dataSet) as t1:
        data_1 = json.load(t1)

    controllers = []
    counter = 0

    for data_i in data_1["ControllerName"].values():
        controllers.append([])
        controllers[counter].append(data_i)
        counter += 1

    counter = 0
    for data_i in data_1["DebugLogCount"].values():
        controllers[counter].append(data_i)
        counter += 1

    table_data = controllers

    columns = ["Component Name", "Debug Logs Count"]
    fig = plt.figure(figsize=(11, 6))
    ax = fig.subplots()
    table = ax.table(cellText=table_data, colWidths=[0.6, 0.25], colLabels=columns, loc='center', cellLoc='left')
    table.auto_set_font_size(False)
    table.set_fontsize(8.5)
    table.scale(1.5, 2.2)
    ax.axis('off')
    fig.tight_layout()
    ax.set_title(title, y=1.5)
    plt.savefig(imageName)

tabelCreator("debug_logs_production.json", "Top 20 Components with DEBUG logs over Past Week (Production)", "debug_logs_production.png")
tabelCreator("debug_logs_stage.json", "Top 20 Components with DEBUG logs over Past Week (Stage)", "debug_logs_stage.png")

created_imageList = ["debug_logs_production.png", "debug_logs_stage.png"]
created_csvList = ["debug_logs_production.csv", "debug_logs_stage.csv"]

emailBody = emailBodyGenerate()

sendEmail(emailBody, created_imageList, created_csvList)
