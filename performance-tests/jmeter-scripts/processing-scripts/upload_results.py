import os
import argparse
from datetime import datetime
from google.auth.transport.requests import Request
from google.oauth2.credentials import Credentials
from google_auth_oauthlib.flow import InstalledAppFlow
from googleapiclient.discovery import build
from googleapiclient.errors import HttpError
from googleapiclient.http import MediaFileUpload

# If modifying these scopes, delete the file token.json.
LOCAL_RESULTS_FOLDER = "results"


def authenticate_drive_api():
    """Authenticate and return the Google Drive API service."""
    creds = None
    token_content = os.environ.get('GOOGLE_TOKEN')

    if token_content is None:
        print("Error: The GOOGLE_TOKEN environment variable is not set.")
        exit(1)

    credentials_file = 'token.json'
    
    with open(credentials_file, 'w') as file:
        file.write(token_content)

    # Load the credentials from the JSON file
    creds = Credentials.from_authorized_user_file(credentials_file)
    return build("drive", "v3", credentials=creds)


def get_folder_id_by_name(service, folder_name, parent_folder_id=None):
    """Resolve a folder name to its ID."""
    try:
        if parent_folder_id:
            query = f"'{parent_folder_id}' in parents and mimeType = 'application/vnd.google-apps.folder' and name = '{folder_name}'"
        else:
            query = f"mimeType = 'application/vnd.google-apps.folder' and name = '{folder_name}'"
        results = service.files().list(
            q=query,
            spaces="drive",
            fields="files(id, name)"
        ).execute()
        items = results.get("files", [])
        if not items:
            print(f"Folder '{folder_name}' not found.")
            return None
        return items[0]["id"]
    except HttpError as error:
        print(f"An error occurred while searching for the folder: {error}")
        return None




def create_folder(service, parent_folder_id, folder_name):
    """Create a folder with a given name in the parent folder."""
    try:
        file_metadata = {
            "name": folder_name,
            "mimeType": "application/vnd.google-apps.folder",
            "parents": [parent_folder_id],
        }
        folder = service.files().create(body=file_metadata, fields="id").execute()
        print(f"Folder '{folder_name}' created with ID: {folder['id']}")
        return folder["id"]
    except HttpError as error:
        print(f"An error occurred while creating the folder: {error}")
        return None


def is_folder_empty(service, folder_id):
    """Check if a folder is empty."""
    try:
        query = f"'{folder_id}' in parents"
        results = service.files().list(
            q=query,
            spaces="drive",
            fields="files(id, name)"
        ).execute()
        items = results.get("files", [])
        return len(items) == 0
    except HttpError as error:
        print(f"An error occurred while checking folder contents: {error}")
        return False


def upload_files(service, folder_id, local_folder_path):
    """Upload all files from a local folder to a Google Drive folder."""
    try:
        for file_name in os.listdir(local_folder_path):
            file_path = os.path.join(local_folder_path, file_name)
            if os.path.isfile(file_path):  # Ensure it's a file
                print(f"Uploading '{file_name}' to Google Drive...")
                file_metadata = {
                    "name": file_name,
                    "parents": [folder_id]
                }
                media = MediaFileUpload(file_path, resumable=True)
                file = service.files().create(
                    body=file_metadata, media_body=media, fields="id"
                ).execute()
                print(f"Uploaded '{file_name}' with ID: {file['id']}")
    except Exception as e:
        print(f"An error occurred while uploading files: {e}")


def write_url_to_file(folder_name, url):
    """Write the folder URL to a file named after the folder."""
    file_name = f"{folder_name}.txt"
    try:
        # Write the URL without additional characters
        with open(file_name, "w", encoding="utf-8") as f:
            f.write(url.strip())  # Strip any accidental whitespace
        print(f"URL written to file '{file_name}'")
    except Exception as e:
        print(f"An error occurred while writing URL to file: {e}")


def main(trigger_deploy_folder_name):
    """Main function to manage folders and upload files."""
    try:
        service = authenticate_drive_api()

        # Step 1: Resolve the folder ID of 'trigger-deploy'
        trigger_deploy_folder_id = get_folder_id_by_name(service, trigger_deploy_folder_name)
        if not trigger_deploy_folder_id:
            print(f"The folder '{trigger_deploy_folder_name}' does not exist.")
            return

        # Format the current date as 'DD-MM-YYYY'
        today_date = datetime.now().strftime("%d-%m-%Y")

        # Step 2: Check/Create the date folder
        date_folder_id = get_folder_id_by_name(service, today_date, parent_folder_id=trigger_deploy_folder_id)
        if not date_folder_id:
            print(f"Folder for today's date '{today_date}' does not exist. Creating it...")
            date_folder_id = create_folder(service, trigger_deploy_folder_id, today_date)

        # Step 3: Check/Create the `run-n` folder
        run_index = 1
        while True:
            run_folder_name = f"run-{run_index}"
            run_folder_id = get_folder_id_by_name(service, run_folder_name, parent_folder_id=date_folder_id)
            if not run_folder_id:
                # If folder does not exist, create it
                print(f"Creating folder '{run_folder_name}'...")
                run_folder_id = create_folder(service, date_folder_id, run_folder_name)
                break
            else:
                # If folder exists, check if it's empty
                if is_folder_empty(service, run_folder_id):
                    print(f"Folder '{run_folder_name}' exists and is empty. Using it.")
                    break
                else:
                    print(f"Folder '{run_folder_name}' exists but is not empty. Checking next.")
                    run_index += 1

        # Step 4: Output the URL for the `run-n` folder
        run_folder_url = f"https://drive.google.com/drive/folders/{run_folder_id}"
        print(f"URL for '{run_folder_name}': {run_folder_url}")

        # Step 5: Write the URL to a file named after the folder
        write_url_to_file(trigger_deploy_folder_name, run_folder_url)

        # Step 6: Upload files to the `run-n` folder
        print(f"Uploading files from local folder '{LOCAL_RESULTS_FOLDER}' to '{run_folder_name}'...")
        upload_files(service, run_folder_id, LOCAL_RESULTS_FOLDER)

    except Exception as e:
        print(f"An unexpected error occurred: {e}")


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Manage Google Drive folders and uploads.")
    parser.add_argument("trigger_deploy_folder_name", help="The human-readable name of the 'trigger-deploy' folder.")
    args = parser.parse_args()

    main(args.trigger_deploy_folder_name)
