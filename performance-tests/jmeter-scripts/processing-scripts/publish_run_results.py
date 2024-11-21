import os
import csv
import sys
from google.auth.transport.requests import Request
from google.oauth2.credentials import Credentials
from google_auth_oauthlib.flow import InstalledAppFlow
from googleapiclient.discovery import build
from googleapiclient.errors import HttpError

from datetime import datetime
from gspread.exceptions import APIError

# The ID of your spreadsheet
SAMPLE_SPREADSHEET_ID = "1sM_UfSTZ88fadSDXIWxLrPsmRhYUyCX1qVCP2Rm6BH0"

# Name of the sheet where the data will be written
TARGET_SHEET_NAME = "API Invocations"
# TARGET_SHEET_NAME = "Class Data"


def read_token():
    creds = None
    if os.path.exists("token.json"):
        creds = Credentials.from_authorized_user_file("token.json", SCOPES)
        return creds
    if not creds or not creds.valid:
        if creds and creds.expired and creds.refresh_token:
            creds.refresh(Request())
        else:
            flow = InstalledAppFlow.from_client_secrets_file(
                "credentials.json", SCOPES
            )
            creds = flow.run_local_server(port=0)
        with open("token.json", "w") as token:
            token.write(creds.to_json())
        return creds


def prepend_data_with_merge(file_path):

    token_content = os.environ.get('GSHEET_TOKEN')

    if token_content is None:
        print("Error: The GSHEET_TOKEN environment variable is not set.")

    credentials_file = 'token.json'

    SAMPLE_SPREADSHEET_ID = "1NOmSI5QnyPczG2efSN5D_yoO3xXyP0GuHYMEYqREwhg"

    with open(credentials_file, 'w') as file:
        file.write(token_content)

    # Load the credentials from the JSON file
    creds = Credentials.from_authorized_user_file(credentials_file)

    """Prepends CSV content with a timestamp to a specific Google Sheet, adds a buffer row, and merges cells."""
    # creds = read_token()
    try:
        service = build("sheets", "v4", credentials=creds)

        # Ensure the target sheet exists
        create_sheet_if_not_exists(service, SAMPLE_SPREADSHEET_ID, TARGET_SHEET_NAME)

        # Read existing data from the sheet
        sheet = service.spreadsheets()
        result = sheet.values().get(
            spreadsheetId=SAMPLE_SPREADSHEET_ID, range=f"{TARGET_SHEET_NAME}!A2:Z"
        ).execute()
        existing_values = result.get("values", [])

        # Read the new data from the CSV file
        new_data = []
        with open(file_path, mode="r") as file:
            reader = csv.reader(file)
            new_data = list(reader)

        new_data = new_data[1:]  # Skip the header row

        # Add a timestamp column to the new data
        timestamp = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
        new_data_with_timestamp = [[timestamp] + row for row in new_data]

        # Add an empty row as a buffer
        empty_row = ["" for _ in range(len(new_data_with_timestamp[0]))]  # Create an empty row
        updated_values = new_data_with_timestamp + [empty_row] + existing_values

        # Write the updated data back to the sheet
        body = {"values": updated_values}
        sheet.values().update(
            spreadsheetId=SAMPLE_SPREADSHEET_ID,
            range=f"{TARGET_SHEET_NAME}!B2",
            valueInputOption="RAW",
            body=body,
        ).execute()

        # Merge the timestamp cell for the new data (spanning 3 rows)
        merge_request = {
            "requests": [
                {
                    "mergeCells": {
                        "range": {
                            "sheetId": None,  # Sheet ID will be resolved dynamically
                            "startRowIndex": 1,  # Account for the empty row buffer
                            "endRowIndex": 3,  # Merge spans 3 rows after the buffer
                            "startColumnIndex": 0,
                            "endColumnIndex": 1,  # First column only
                        },
                        "mergeType": "MERGE_ALL",
                    }
                }
            ]
        }

        # Resolve the sheet ID dynamically
        spreadsheet = service.spreadsheets().get(spreadsheetId=SAMPLE_SPREADSHEET_ID).execute()
        sheets = spreadsheet.get("sheets", [])
        sheet_id = next(
            sheet.get("properties", {}).get("sheetId")
            for sheet in sheets
            if sheet.get("properties", {}).get("title") == TARGET_SHEET_NAME
        )

        # Update the merge request with the resolved sheet ID
        merge_request["requests"][0]["mergeCells"]["range"]["sheetId"] = sheet_id

        # Execute the merge request
        service.spreadsheets().batchUpdate(
            spreadsheetId=SAMPLE_SPREADSHEET_ID, body=merge_request
        ).execute()

        print("Data prepended with a buffer row and cells merged successfully.")

    except HttpError as err:
        print(f"An error occurred: {err}")


def create_sheet_if_not_exists(service, spreadsheet_id, sheet_name):
    """Creates the sheet if it does not exist."""
    try:
        # Get the spreadsheet metadata
        spreadsheet = service.spreadsheets().get(spreadsheetId=spreadsheet_id).execute()
        sheets = spreadsheet.get("sheets", [])

        # Check if the sheet already exists
        for sheet in sheets:
            if sheet.get("properties", {}).get("title") == sheet_name:
                return  # Sheet already exists

        # If the sheet does not exist, create it
        batch_update_request_body = {
            "requests": [
                {
                    "addSheet": {
                        "properties": {"title": sheet_name},
                    }
                }
            ]
        }
        service.spreadsheets().batchUpdate(
            spreadsheetId=spreadsheet_id, body=batch_update_request_body
        ).execute()
        print(f"Sheet '{sheet_name}' created.")
    except HttpError as err:
        print(f"An error occurred while creating the sheet: {err}")


if __name__ == "__main__":
    # Path to the uploaded file

    if len(sys.argv) < 2:
        print("Usage: python3 your_script.py <file_path>")
        sys.exit(1)

    file_path = sys.argv[1]

    # Verify that the file exists
    if not os.path.exists(file_path):
        print(f"Error: File '{file_path}' does not exist.")
        sys.exit(1)

    # write_to_sheet(file_path)
    prepend_data_with_merge(file_path);
    # write_to_gsheet(file_path,TARGET_SHEET_NAME)
