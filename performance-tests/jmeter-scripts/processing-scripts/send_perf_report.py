import os
import sys
from datetime import datetime, timedelta
from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText
from email.mime.image import MIMEImage
import smtplib


def get_week_monday(date_str):
    """
    Calculate the Monday of the week for the given date.
    :param date_str: The date string in the format 'YYYY-MM-DD HH:MM:SS'
    :return: The date string for the Monday in the format 'YYYY-MM-DD'
    """
    date = datetime.strptime(date_str, "%Y-%m-%d %H:%M:%S")
    monday = date - timedelta(days=date.weekday())  # Subtract days to get to Monday
    return monday.strftime("%Y-%m-%d")

# List of all required and optional images
images = {
    "20_tps_chart": "20_tps_chart.png",
    "20_latency_chart": "20_latency_chart.png",
    "200_tps_chart": "200_tps_chart.png",
    "200_latency_chart": "200_latency_chart.png",
    "200_error_chart": "200_error_chart.png",  # Optional
    "api_invocations_tps_chart": "api_invocations_tps_chart.png",
    "api_invocations_latency_chart": "api_invocations_latency_chart.png",
    "api_invocations_error_chart": "api_invocations_error_chart.png",  # Optional
}

def check_images_exist(image_dict):
    """
    Check if required images exist. Ignore missing images containing "error" in their filename.
    Returns a dictionary of existing images and missing ones.
    """
    existing_images = {}
    missing_images = []

    for name, path in image_dict.items():
        if os.path.exists(path):
            existing_images[name] = path
        elif "error" in name:  # Skip missing error images
            print(f"Skipping missing error image: {name}")
        else:
            missing_images.append(name)

    if missing_images:
        print(f"Missing critical images: {', '.join(missing_images)}. Email will still be sent.")

    return existing_images, missing_images

def create_email_body(images):
    # Extract image CIDs
    tps_20 = images.get('20_tps_chart', '')
    latency_20 = images.get('20_latency_chart', '')
    tps_200 = images.get('200_tps_chart', '')
    latency_200 = images.get('200_latency_chart', '')
    error_200 = images.get('200_error_chart', '')
    tps_api = images.get('api_invocations_tps_chart', '')
    latency_api = images.get('api_invocations_latency_chart', '')
    error_api = images.get('api_invocations_error_chart', '')

    email_body = f'''<!DOCTYPE html>
<html>
<head>
<style>
    body {{ font-family: Arial, sans-serif; font-size: 14px; line-height: 1.6; margin: 10px; }}
    img.chart {{ max-width: 45%; height: auto; margin: 10px 0; }}
    .chart-container {{ display: flex; flex-wrap: wrap; gap: 20px; margin-top: 10px; justify-content: space-between; }}
    .main-title {{ font-size: 18px; font-weight: bold; margin-top: 30px; border-bottom: 2px solid #ccc; padding-bottom: 5px; }}
    .section-title {{ font-size: 16px; font-weight: bold; margin-top: 20px; }}
    .subtitle {{ font-size: 14px; font-weight: bold; margin-top: 10px; }}
    table {{ border-collapse: collapse; width: 30%; margin: 20px 0; border: 1px solid #ddd; }}
    th, td {{ border: 1px solid #ddd; text-align: left; padding: 8px; }}
    th {{ background-color: #f2f2f2; }}
    tr:hover {{ background-color: #f1f1f1; }}
    p {{ margin-top: 10px; }}
    a {{ color: #1a73e8; text-decoration: none; }}
    a:hover {{ text-decoration: underline; }}
    .footer {{ margin-top: 20px; font-size: 12px; color: #555; }}
    .separator {{ margin-top: 30px; border-top: 2px dashed #ccc; }}
</style>
</head>
<body>
    <p>Dear All,</p>
    <p>
        Please find the performance comparison for main operations in Choreo.<br>
        All these tests were performed in the staging environment.
    </p>

    <!-- Control Plane Operations Section -->
    <div class="main-title">Control Plane Operations</div>
    <p>
        The following results show the performance of main operations in the Control plane.<br>
        The tests were conducted at two concurrency levels: 20 and 200.<br>
        If an error percentage greater than 1% was recorded for a test,<br>
        it is indicated in the Error Rate chart.
    </p>

    <div class="section-title">Results for 20 Concurrency</div>
    <div class="subtitle">TPS Variation</div>
    <div class="chart-container">
        <img src="cid:{tps_20}" alt="20 TPS Chart" class="chart">
    </div>
    <div class="subtitle">Latency Variation</div>
    <div class="chart-container">
        <img src="cid:{latency_20}" alt="20 Latency Chart" class="chart">
    </div>

    <div class="section-title">Results for 200 Concurrency</div>
    <div class="subtitle">TPS and Error Variation</div>
    <div>
        <div>
            <img src="cid:{tps_200}" alt="200 TPS Chart" class="chart">
        </div>
        {"<div><img src='cid:" + error_200 + "' alt='200 Error Chart' class='chart'></div>" if error_200 else ""}
    </div>
    <div class="subtitle">Latency Variation</div>
    <div class="chart-container">
        <img src="cid:{latency_200}" alt="200 Latency Chart" class="chart">
    </div>

    <div class="separator"></div>

    <!-- Data Plane Operations Section -->
    <div class="main-title">Data Plane Operations</div>
    <div class="section-title">API Invocations</div>
    <p>
        The following results pertain to API Invocations.<br>
        This test was conducted with a concurrency level of 300, using a service deployed on Choreo as the backend.
    </p>

    <div class="subtitle">Resources Used</div>
    <table>
        <tr>
            <th>Component</th>
            <th>CPU</th>
            <th>Memory</th>
        </tr>
        <tr>
            <td>Nginx</td>
            <td>500m</td>
            <td>1536MiB</td>
        </tr>
        <tr>
            <td>Choreo-Connect</td>
            <td>3000m</td>
            <td>7168MiB</td>
        </tr>
    </table>

    <div class="subtitle">Throughput and Error Variation</div>
    <div>
        <div>
            <img src="cid:{tps_api}" alt="API Invocations TPS Chart" class="chart">
        </div>
        {"<div><img src='cid:" + error_api + "' alt='API Invocations Error Chart' class='chart'></div>" if error_api else ""}
    </div>
    <div class="subtitle">Latency Variation</div>
    <div class="chart-container">
        <img src="cid:{latency_api}" alt="API Invocations Latency Chart" class="chart">
    </div>

    <div class="footer">
        <b>
        You can find detailed results and additional information in the 
        <a href="https://docs.google.com/spreadsheets/d/1sM_UfSTZ88fadSDXIWxLrPsmRhYUyCX1qVCP2Rm6BH0/edit?usp=sharing" target="_blank">
        Google Sheet</a>.
        </b>
        <p>If you have any questions or require further clarification, please contact amilad@wso2.com.</p>
    </div>
</body>
</html>
'''
    return email_body


def send_email():
    """
    Create and send the email with charts embedded as inline images.
    """
    # missing_images = check_images_exist(images)
    existing_images, missing_images = check_images_exist(images)
    if missing_images:
        print("Missing images detected. Aborting email sending.")
        return

    sender = "choreo-automation-bot@wso2.com"
    if len(sys.argv) < 2:
        print("Usage: python3 send_perf_mail.py <comma-separated-email-list>")
        return

    receiver_input = sys.argv[1]
    receiver = [email.strip() for email in receiver_input.split(",")]

    email_user = os.environ.get('EMAIL_USER')
    email_password = os.environ.get('EMAIL_PASSWORD')

    # Create the email message
    msg = MIMEMultipart('related')
    timestamp = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    msg['Subject'] = "[Choreo] Performance Test Results as at " + get_week_monday(timestamp)
    msg['From'] = sender
    msg['To'] = ", ".join(receiver)

    # Create a dictionary to store image CIDs
    # image_cids = {key: key for key in images.keys()}
    image_cids = {key: key for key in existing_images.keys()}

    # Attach the email body
    email_body = create_email_body(image_cids)
    msgText = MIMEText(email_body, 'html')
    msg.attach(msgText)

    # Attach all images
    for cid, path in existing_images.items():
        if os.path.exists(path):  # Attach only existing images
            with open(path, 'rb') as img_file:
                image = MIMEImage(img_file.read(), name=os.path.basename(path))
                image.add_header('Content-ID', f'<{cid}>')
                image.add_header('Content-Disposition', 'inline', filename=os.path.basename(path))
                msg.attach(image)

    # Send the email via SMTP server
    try:
        s = smtplib.SMTP('email-smtp.us-east-1.amazonaws.com', 587)
        s.ehlo()
        s.starttls()
        s.login(email_user, email_password)
        s.sendmail(sender, receiver, msg.as_string())
        print("Email sent successfully.")
    except Exception as e:
        print(f"Failed to send email: {e}")
    finally:
        s.quit()

if __name__ == "__main__":
    send_email()
