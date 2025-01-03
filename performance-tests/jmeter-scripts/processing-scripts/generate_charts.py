import os
import sys
import matplotlib.pyplot as plt
import numpy as np
from google.oauth2.credentials import Credentials
from googleapiclient.discovery import build
from googleapiclient.errors import HttpError
import matplotlib.colors as mcolors

SAMPLE_SPREADSHEET_ID = "1sM_UfSTZ88fadSDXIWxLrPsmRhYUyCX1qVCP2Rm6BH0"
TARGET_SHEET_NAME = "Summary"

# Reusable: Fetch data from a specified range
def fetch_data(service, range_name):
    try:
        result = service.spreadsheets().values().get(
            spreadsheetId=SAMPLE_SPREADSHEET_ID, range=f"{TARGET_SHEET_NAME}!{range_name}"
        ).execute()
        values = result.get("values", [])
        print(f"Fetched data from range {range_name}: {values}")
        return values
    except HttpError as err:
        print(f"An error occurred while fetching data: {err}")
        return None

# Reusable: Delete existing charts
def delete_existing_charts(service, sheet_id):
    try:
        spreadsheet = service.spreadsheets().get(spreadsheetId=SAMPLE_SPREADSHEET_ID).execute()
        sheets = spreadsheet.get("sheets", [])
        delete_requests = []

        for sheet in sheets:
            if sheet["properties"]["sheetId"] == sheet_id:
                if "charts" in sheet:
                    for chart in sheet["charts"]:
                        delete_requests.append(
                            {"deleteEmbeddedObject": {"objectId": chart["chartId"]}}
                        )

        if delete_requests:
            service.spreadsheets().batchUpdate(
                spreadsheetId=SAMPLE_SPREADSHEET_ID, body={"requests": delete_requests}
            ).execute()
            print(f"Deleted {len(delete_requests)} existing chart(s) from the Progress sheet.")
        else:
            print("No existing charts found in the Progress sheet.")
    except HttpError as err:
        print(f"An error occurred while deleting charts: {err}")

def create_api_invocation_chart(data, title, ylabel, output_file):
    """
    Creates a grouped bar chart for API invocation data with the most recent 4 days
    as the x-axis and multiple rows as separate series in the chart.
    """
    import matplotlib.colors as mcolors

    header = data[0]  # First row is the header (dates)
    rows = data[1:]   # Remaining rows contain the data

    # Select the most recent 4 days (columns)
    date_columns = header[2:]  # Dates start from the third column
    last_four_dates = date_columns[-4:]  # Select the most recent 4 dates

    # Extract labels and data for the last 4 days
    labels = [row[1] for row in rows]  # Labels are in column B
    all_values = [[float(row[header.index(date)]) for date in last_four_dates] for row in rows]

    x = np.arange(len(last_four_dates))  # X-axis positions for the last 4 days
    width = 0.2  # Width of each bar

    # Define a color palette
    color_palette = list(mcolors.TABLEAU_COLORS.values())

    # Create the plot
    fig, ax = plt.subplots(figsize=(12, 8))

    # Plot each row of data as a separate series
    for i, (label, values) in enumerate(zip(labels, all_values)):
        color = color_palette[i % len(color_palette)]  # Cycle through colors if needed
        ax.bar(x + (i - len(rows)/2) * width, values, width, label=label, color=color, zorder=3)

    # Set a light grey background
    ax.set_facecolor('#f7f7f7')

    # Enable grid lines for the Y-axis
    ax.yaxis.grid(color='black', linestyle='-', linewidth=0.7, zorder=1)
    ax.xaxis.set_major_locator(plt.MultipleLocator(1))  # Ensure one grid line per bar
    ax.xaxis.grid(color='black', which='both', linestyle='-', linewidth=0.7, zorder=1)  # X-axis grid
    ax.set_axisbelow(True)  # Ensure grid lines are below the bars

    # Add labels, title, and formatting
    ax.set_ylabel(ylabel, fontsize=12)
    ax.set_title(title, fontsize=14, weight='bold')
    ax.set_xticks(x)
    ax.set_xticklabels(last_four_dates, rotation=45, ha='right', fontsize=10)

    # Add legend with transparent background
    legend = ax.legend(frameon=True, loc='upper left', fontsize=10)
    legend.get_frame().set_alpha(0.8)  # Transparent legend background
    legend.get_frame().set_facecolor('#eeeeee')

    # Finalize and save the chart
    plt.tight_layout()
    plt.savefig(output_file)
    print(f"Chart saved as {output_file}")
    return output_file

# Reusable: Add a chart for API invocations to Google Sheets
def add_api_invocation_chart_to_sheet(service, sheet_id, data_range, title, ylabel):
    """
    Adds a bar chart to the Google Sheet for API invocation data, considering only the last 4 days.
    """
    # Parse data_range to calculate row and column indices
    range_start, range_end = data_range.split(":")
    start_column, start_row = range_start[:1], int(range_start[1:]) - 1
    end_column, end_row = range_end[:1], int(range_end[1:]) - 1

    # Fetch the data and determine the last 4 days
    full_data = fetch_data(service, f"{start_column}{start_row + 1}:{end_column}{end_row + 1}")
    header = full_data[0]  # Header row (dates)
    date_columns = header[2:]  # Dates start from the third column
    last_four_dates = date_columns[-4:]  # Select the most recent 4 dates
    last_four_start_column = chr(ord(start_column) + 2 + len(date_columns) - 4)  # Start column for the last 4 dates
    last_four_end_column = chr(ord(last_four_start_column) + 3)  # End column for the last 4 dates

    # Update the chart range to consider only the last 4 days
    chart_data_range = f"{start_column}{start_row + 1}:{last_four_end_column}{end_row + 1}"

    # Prepare series requests for the last 4 days
    series_requests = [
        {
            "series": {
                "sourceRange": {
                    "sources": [
                        {
                            "sheetId": sheet_id,
                            "startRowIndex": start_row,
                            "endRowIndex": end_row + 1,
                            "startColumnIndex": ord(last_four_start_column) - ord('A') + i,
                            "endColumnIndex": ord(last_four_start_column) - ord('A') + i + 1,
                        }
                    ]
                }
            },
            "targetAxis": "LEFT_AXIS",
            "color": {
                "red": (i * 0.2) % 1.0,
                "green": (i * 0.4) % 1.0,
                "blue": (i * 0.6) % 1.0,
            },
        }
        for i in range(4)  # For the last 4 days
    ]

    # Define chart request
    chart_request = {
        "requests": [
            {
                "addChart": {
                    "chart": {
                        "spec": {
                            "title": title,
                            "basicChart": {
                                "chartType": "COLUMN",
                                "legendPosition": "TOP_LEGEND",
                                "headerCount": 1,
                                "axis": [{"position": "LEFT_AXIS", "title": ylabel}],
                                "domains": [
                                    {
                                        "domain": {
                                            "sourceRange": {
                                                "sources": [
                                                    {
                                                        "sheetId": sheet_id,
                                                        "startRowIndex": start_row,
                                                        "endRowIndex": end_row + 1,
                                                        "startColumnIndex": ord(start_column) - ord('A') + 1,
                                                        "endColumnIndex": ord(start_column) - ord('A') + 2,
                                                    }
                                                ]
                                            }
                                        }
                                    }
                                ],
                                "series": series_requests,
                            }
                        },
                        "position": {
                            "overlayPosition": {
                                "anchorCell": {
                                    "sheetId": sheet_id,
                                    "rowIndex": end_row + 5,
                                    "columnIndex": 7,
                                }
                            }
                        },
                    }
                }
            }
        ]
    }
    response = service.spreadsheets().batchUpdate(
        spreadsheetId=SAMPLE_SPREADSHEET_ID, body=chart_request
    ).execute()
    print(f"Chart '{title}' added successfully to Google Sheets: {response}")

# Reusable: Create grouped bar chart locally
def create_grouped_bar_chart(data, title, ylabel, output_file):

    header = data[0]
    rows = data[1:]

    # Extract the steps and the last four date columns
    steps = [row[1] for row in rows]
    date_columns = header[2:]
    last_four_dates = date_columns[-4:]

    # Prepare data for the grouped bar chart
    chart_data = []
    for row in rows:
        chart_row = [float(row[header.index(date)]) for date in last_four_dates]
        chart_data.append(chart_row)

    x = np.arange(len(steps))  # Positions for the steps
    width = 0.2  # Width of each bar in the group

    # Define a subtle color palette (pastel colors)
    color_palette = list(mcolors.TABLEAU_COLORS.values())[:len(last_four_dates)]

    # Create the plot
    fig, ax = plt.subplots(figsize=(12, 8))
    for i, (date, color) in enumerate(zip(last_four_dates, color_palette)):
        data_for_date = [chart_row[i] for chart_row in chart_data]
        ax.bar(
            x + (i - 1.5) * width,
            data_for_date,
            width,
            label=date,
            color=color,
            zorder=3
        )  # Bars above the grid

    # Set a light grey background
    ax.set_facecolor('#f7f7f7')

    # Enable grid lines for both axes
    ax.yaxis.grid(color='black', linestyle='-', linewidth=0.7, zorder=1)  # Y-axis grid
    ax.xaxis.set_major_locator(plt.MultipleLocator(1))  # Ensure one grid line per bar group
    ax.xaxis.grid(color='black', linestyle='-', linewidth=0.7, zorder=1)  # X-axis grid
    ax.set_axisbelow(True)  # Ensure grid lines are below the bars

    # Add labels, title, and formatting
    ax.set_ylabel(ylabel, fontsize=12)
    ax.set_title(title, fontsize=14, weight='bold')
    ax.set_xticks(x)
    ax.set_xticklabels(steps, rotation=45, ha="right", fontsize=10)

    # Add legend with transparent background
    legend = ax.legend(frameon=True, loc='upper left', fontsize=10)
    legend.get_frame().set_alpha(0.8)  # Transparent legend background
    legend.get_frame().set_facecolor('#eeeeee')

    # Finalize and save the chart
    plt.tight_layout()
    plt.savefig(output_file)
    print(f"Chart saved as {output_file}")
    return output_file

# Reusable: Add a chart to Google Sheets
def add_chart_to_sheet(service, sheet_id, data_range, title, ylabel):
    # Parse data_range to calculate row and column indices
    range_start, range_end = data_range.split(":")
    start_column, start_row = range_start[:1], int(range_start[1:]) - 1
    end_column, end_row = range_end[:1], int(range_end[1:]) - 1

    # Compute column indices for domains and series
    start_column_index = ord(start_column.upper()) - ord("A") + 1
    series_start_column_index = start_column_index + 1
    series_end_column_index = ord(end_column.upper()) - ord("A") + 1

    # Fetch header
    header = fetch_data(service, f"{start_column}{start_row + 1}:{end_column}{start_row + 1}")[0]
    date_columns = header[2:]
    last_four_dates = date_columns[-4:]

    # Prepare series requests
    series_requests = [
        {
            "series": {
                "sourceRange": {
                    "sources": [
                        {
                            "sheetId": sheet_id,
                            "startRowIndex": start_row,
                            "endRowIndex": end_row + 1,
                            "startColumnIndex": series_start_column_index + i,
                            "endColumnIndex": series_start_column_index + i + 1,
                        }
                    ]
                }
            },
            "targetAxis": "LEFT_AXIS",
            "color": {
                "red": (i * 0.2) % 1.0,
                "green": (i * 0.4) % 1.0,
                "blue": (i * 0.6) % 1.0,
            },
        }
        for i, _ in enumerate(last_four_dates)
    ]

    # Define chart request
    chart_request = {
        "requests": [
            {
                "addChart": {
                    "chart": {
                        "spec": {
                            "title": title,
                            "basicChart": {
                                "chartType": "COLUMN",
                                "legendPosition": "TOP_LEGEND",
                                "headerCount": 1,
                                "axis": [{"position": "LEFT_AXIS", "title": ylabel}],
                                "domains": [
                                    {
                                        "domain": {
                                            "sourceRange": {
                                                "sources": [
                                                    {
                                                        "sheetId": sheet_id,
                                                        "startRowIndex": start_row,
                                                        "endRowIndex": end_row + 1,
                                                        "startColumnIndex": start_column_index,
                                                        "endColumnIndex": start_column_index + 1,
                                                    }
                                                ]
                                            }
                                        }
                                    }
                                ],
                                "series": series_requests,
                            }
                        },
                        "position": {
                            "overlayPosition": {
                                "anchorCell": {"sheetId": sheet_id, "rowIndex": end_row + 5, "columnIndex": 7},
                            }
                        },
                    }
                }
            }
        ]
    }
    response = service.spreadsheets().batchUpdate(
        spreadsheetId=SAMPLE_SPREADSHEET_ID, body=chart_request
    ).execute()
    print(f"Chart '{title}' added successfully to Google Sheets: {response}")

def main():
    token_content = os.environ.get("GSHEET_TOKEN")
    if token_content is None:
        print("Error: The GSHEET_TOKEN environment variable is not set.")
        sys.exit(1)

    credentials_file = "token.json"
    with open(credentials_file, "w") as file:
        file.write(token_content)
    creds = Credentials.from_authorized_user_file(credentials_file)

    service = build("sheets", "v4", credentials=creds)
    spreadsheet = service.spreadsheets().get(spreadsheetId=SAMPLE_SPREADSHEET_ID).execute()
    sheet_id = next(
        (s["properties"]["sheetId"] for s in spreadsheet["sheets"] if s["properties"]["title"] == TARGET_SHEET_NAME),
        None,
    )
    if not sheet_id:
        print("Error: Could not find the sheet ID.")
        return

    delete_existing_charts(service, sheet_id)

    # TPS Chart
    tps_data = fetch_data(service, "B1:Z6")
    if tps_data:
        create_grouped_bar_chart(tps_data, "TPS Variation", "TPS", "200_tps_chart.png")
        add_chart_to_sheet(service, sheet_id, "B1:Z6", "TPS Variation (200 Users)", "TPS")

    # Latency Chart
    latency_data = fetch_data(service, "B8:Z13")
    if latency_data:
        create_grouped_bar_chart(latency_data, "Latency - 99th Percentile", "Latency (ms)", "200_latency_chart.png")
        add_chart_to_sheet(service, sheet_id, "B8:Z13", "Latency Variation (200 Users)", "Latency (ms)")

    # Error rates
    error_data = fetch_data(service, "B15:Z20")
    if error_data:
        create_grouped_bar_chart(error_data, "Error Rate", "Percentage (%)", "200_error_chart.png")
        add_chart_to_sheet(service, sheet_id, "B15:Z20", "Error Rate (200 Users)", "Percentage (%)")

    tps_data = fetch_data(service, "B22:Z27")
    if tps_data:
        create_grouped_bar_chart(tps_data, "TPS Variation", "TPS", "20_tps_chart.png")

    latency_data = fetch_data(service, "B29:Z34")
    if tps_data:
        create_grouped_bar_chart(latency_data, "Latency - 99th Percentile", "Latency (ms)", "20_latency_chart.png")

   # Fetch API invocation data
    api_invocations = fetch_data(service, "B43:Z44")
    if api_invocations:
        create_api_invocation_chart(api_invocations, "API Invocations - Throughput", "TPS", "api_invocations_tps_chart.png")
        add_api_invocation_chart_to_sheet(service, sheet_id, "B43:Z44", "API Invocations", "TPS")

   # Fetch API invocation data
    api_invocations = fetch_data(service, "B46:Z48")
    if api_invocations:
        create_api_invocation_chart(api_invocations, "API Invocations - Latency", "P99 Latency (ms)", "api_invocations_latency_chart.png")
        add_api_invocation_chart_to_sheet(service, sheet_id, "B46:Z48", "API Invocations - Latency", "P99 Latency (ms)")

   # Fetch API invocation data
    api_invocations = fetch_data(service, "B50:Z51")
    if api_invocations:
        create_api_invocation_chart(api_invocations, "API Invocations - Errors", "Errors (%)", "api_invocations_error_chart.png")
        add_api_invocation_chart_to_sheet(service, sheet_id, "B50:Z51", "API Invocations - Errors", "Errors (%)")

if __name__ == "__main__":
    main()
