import requests
import os
import logging


logging.basicConfig(level=logging.INFO)
required_env_variables = [
    "INTEGRATION_TEST_CHAT_SPACE_ID", "INTEGRATION_TEST_FAILURE_BOT_KEY", "INTEGRATION_TEST_FAILURE_BOT_TOKEN",
    "JSON_CHAT_TEMPLATE_FILE",
]

missing_variables = [var for var in required_env_variables if os.environ.get(var) is None]
if missing_variables:
    raise EnvironmentError(f"Missing required environment variables: {', '.join(missing_variables)}")


def send_chat():
    try:
        space_id = os.environ.get("INTEGRATION_TEST_CHAT_SPACE_ID")
        space_key = os.environ.get("INTEGRATION_TEST_FAILURE_BOT_KEY")
        space_token = os.environ.get("INTEGRATION_TEST_FAILURE_BOT_TOKEN")
        google_chat_url = os.environ.get("GOOGLE_CHAT_SPACES_URL", "chat.googleapis.com/v1/spaces")
        json_template_file = os.environ.get("JSON_CHAT_TEMPLATE_FILE")

        with open(json_template_file) as template_file:
                app_message = template_file.read()
        url = f'https://{google_chat_url}/{space_id}/messages?key={space_key}&token={space_token}&threadKey=IntTestNotification'
        message_headers = {"Content-Type": "application/json; charset=UTF-8"}
        response = requests.post(url, app_message, headers=message_headers)
        print(response)
    except Exception as e:
        logging.error(f"An error occurred while sending the chat: {e}")


if __name__ == "__main__":
    send_chat()
