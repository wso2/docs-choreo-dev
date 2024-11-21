import os
import csv
import sys
import re
from google.auth.transport.requests import Request
from google.oauth2.credentials import Credentials
from googleapiclient.discovery import build
from googleapiclient.errors import HttpError
from datetime import datetime, timedelta

SAMPLE_SPREADSHEET_ID = "1sM_UfSTZ88fadSDXIWxLrPsmRhYUyCX1qVCP2Rm6BH0"
TARGET_SHEET_NAME = "API Invocations"

def get_week_monday(date_str):
    """
    Calculate the Monday of the week for the given date.
    :param date_str: The date string in the format 'YYYY-MM-DD HH:MM:SS'
    :return: The date string for the Monday in the format 'YYYY-MM-DD'
    """
    date = datetime.strptime(date_str, "%Y-%m-%d %H:%M:%S")
    monday = date - timedelta(days=date.weekday())  # Subtract days to get to Monday
    return monday.strftime("%Y-%m-%d")


def extract_users_from_log(log_path):
    """Extract the 'users' value from the JMeter log."""
    try:
        with open(log_path, "r") as log_file:
            for line in log_file:
                match = re.search(r"Setting JMeter property: users=(\d+)", line)
                if match:
                    return match.group(1)  # Return the users value
    except FileNotFoundError:
        print(f"Log file not found: {log_path}")
        sys.exit(1)

    print("Users property not found in the log file.")
    sys.exit(1)

def get_drive_link(link_file_path):
    """
    Reads the link from the specified text file.
    :param link_file_path: Path to the text file containing the link
    :return: The link as a string
    """
    try:
        with open(link_file_path, "r") as file:
            link = file.read().strip()
            if not link:
                raise ValueError(f"The link file '{link_file_path}' is empty.")
            return link
    except FileNotFoundError:
        print(f"Error: Link file '{link_file_path}' does not exist.")
        sys.exit(1)

def prepend_data_with_users_and_link(results_folder, link_file_path):
    """Prepends data with week, timestamp, users, and adds a link row under each data row."""
    # Derive file paths for CSV and log files
    csv_file_path = os.path.join(results_folder, "AggregateReport.csv")
    log_file_path = os.path.join(results_folder, "jmeter.log")

    # Ensure both files exist
    if not os.path.exists(csv_file_path):
        print(f"Error: CSV file '{csv_file_path}' does not exist.")
        sys.exit(1)
    if not os.path.exists(log_file_path):
        print(f"Error: Log file '{log_file_path}' does not exist.")
        sys.exit(1)

    # Load credentials from environment variable
    token_content = os.environ.get("GSHEET_TOKEN")
    if token_content is None:
        print("Error: The GSHEET_TOKEN environment variable is not set.")
        sys.exit(1)

    credentials_file = "token.json"
    with open(credentials_file, "w") as file:
        file.write(token_content)
    creds = Credentials.from_authorized_user_file(credentials_file)

    # Extract the 'users' value from the log file
    users_value = extract_users_from_log(log_file_path)

    # Read the drive link from the text file
    drive_link = get_drive_link(link_file_path)

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
        with open(csv_file_path, mode="r") as file:
            reader = csv.reader(file)
            new_data = list(reader)

        new_data = new_data[1:]  # Skip the header row

        # Add week, timestamp, users, and link rows
        timestamp = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
        week = get_week_monday(timestamp)  # Compute the week (Monday)

        # Prepare data with the link row
        new_data_with_metadata = []
        for row in new_data:
            # Normal data row
            data_row = [week, timestamp, users_value] + row
            new_data_with_metadata.append(data_row)

        new_data_with_metadata[1][2] = drive_link
        # Add an empty row as a buffer
        empty_row = ["" for _ in range(len(new_data_with_metadata[0]))]
        updated_values = new_data_with_metadata + [empty_row] + existing_values

        # Write the updated data back to the sheet
        body = {"values": updated_values}
        sheet.values().update(
            spreadsheetId=SAMPLE_SPREADSHEET_ID,
            range=f"{TARGET_SHEET_NAME}!A2",
            valueInputOption="RAW",
            body=body,
        ).execute()

        merge_request = {
            "requests": [
                {
                    "mergeCells": {
                        "range": {
                            "sheetId": None,  # Sheet ID will be resolved dynamically
                            "startRowIndex": 1,
                            "endRowIndex": 3,
                            "startColumnIndex": 0,
                            "endColumnIndex": 1,  # Week column
                        },
                        "mergeType": "MERGE_ALL",
                    }
                },
                {
                    "mergeCells": {
                        "range": {
                            "sheetId": None,
                            "startRowIndex": 1,
                            "endRowIndex": 3,
                            "startColumnIndex": 1,
                            "endColumnIndex": 2,  # Timestamp column
                        },
                        "mergeType": "MERGE_ALL",
                    }
                },
            ]
        }

        spreadsheet = service.spreadsheets().get(spreadsheetId=SAMPLE_SPREADSHEET_ID).execute()
        sheets = spreadsheet.get("sheets", [])
        sheet_id = next(
            sheet.get("properties", {}).get("sheetId")
            for sheet in sheets
            if sheet.get("properties", {}).get("title") == TARGET_SHEET_NAME
        )

        # Update the merge request with the resolved sheet ID
        for request in merge_request["requests"]:
            request["mergeCells"]["range"]["sheetId"] = sheet_id

        # Execute the merge request
        service.spreadsheets().batchUpdate(
            spreadsheetId=SAMPLE_SPREADSHEET_ID, body=merge_request
        ).execute()


        print("Data prepended with week, timestamp, users, and link rows successfully.")

    except HttpError as err:
        print(f"An error occurred: {err}")


def create_sheet_if_not_exists(service, spreadsheet_id, sheet_name):
    """Creates the sheet if it does not exist."""
    try:
        spreadsheet = service.spreadsheets().get(spreadsheetId=spreadsheet_id).execute()
        sheets = spreadsheet.get("sheets", [])
        for sheet in sheets:
            if sheet.get("properties", {}).get("title") == sheet_name:
                return
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
    if len(sys.argv) < 3:
        print("Usage: python3 write_to_sheet.py <results_folder> <link_file_path>")
        sys.exit(1)

    results_folder = sys.argv[1]
    link_file_path = sys.argv[2]

    if not os.path.exists(results_folder):
        print(f"Error: Results folder '{results_folder}' does not exist.")
        sys.exit(1)

    if not os.path.exists(link_file_path):
        print(f"Error: Link file '{link_file_path}' does not exist.")
        sys.exit(1)

    prepend_data_with_users_and_link(results_folder, link_file_path)
