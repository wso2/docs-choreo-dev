import xml.etree.ElementTree as ET
import json
import csv
import base64
import datetime
import argparse

def extract_jwt_claim(auth_header):
    """Extracts and decodes the JWT token to retrieve the organization handle."""
    try:
        if not auth_header or "Bearer " not in auth_header:
            return "unknown"

        token = auth_header.split("Bearer ", 1)[1].strip()
        token_parts = token.split(".")

        if len(token_parts) < 2:
            return "unknown"

        payload_part = token_parts[1]
        padding = len(payload_part) % 4
        if padding:
            payload_part += "=" * (4 - padding)  # Adjust padding for base64 decoding

        decoded_payload = json.loads(base64.urlsafe_b64decode(payload_part).decode("utf-8"))
        return decoded_payload.get("organization", {}).get("handle", "unknown")
    except Exception as e:
        print(f"Error decoding JWT: {e}")
        return "unknown"

def extract_builds(response_json):
    """Extracts the initial and last build details from the response JSON."""
    if not isinstance(response_json, dict):
        return None, None

    builds = response_json.get("data", {}).get("deploymentStatusByVersion", [])

    if not isinstance(builds, list) or not builds:
        return None, None

    # Sort builds by 'started_at' timestamp
    builds.sort(key=lambda x: x.get("started_at", ""))

    initial_build = builds[0] if builds else None
    last_build = builds[-1] if builds else None

    return initial_build, last_build

def process_jtl_file(jtl_file, output_csv, build_filter):
    """Parses the JTL file, extracts relevant data, and saves it to CSV."""
    tree = ET.parse(jtl_file)
    root = tree.getroot()

    rows = []

    for sample in root.findall("httpSample"):
        rc = sample.get("rc")  # Response code
        lb = sample.get("lb")  # Label

        if rc != "200" or lb != "Go API - Get Build Status":
            continue

        request_header = sample.find("requestHeader").text if sample.find("requestHeader") is not None else ""
        response_data = sample.find("responseData").text if sample.find("responseData") is not None else ""

        # Extract JWT and organization handle
        auth_header = ""
        for line in request_header.split("\n"):
            if line.startswith("Authorization: Bearer"):
                auth_header = line.strip()
                break

        org_handle = extract_jwt_claim(auth_header)

        # Extract response JSON
        try:
            response_json = json.loads(response_data.replace("&quot;", "\"") if response_data else "{}")
        except json.JSONDecodeError:
            response_json = {}

        # Extract build details
        initial_build, last_build = extract_builds(response_json)

        for build, build_type in [(initial_build, "initialBuild"), (last_build, "lastBuild")]:
            if build and (build_filter is None or build_type == build_filter):
                rows.append([
                    org_handle,
                    build.get("id", ""),
                    build_type,
                    build.get("started_at", ""),
                    build.get("completed_at", ""),
                    build.get("status", ""),
                    build.get("conclusion", "")
                ])

    # Write to CSV
    with open(output_csv, "w", newline="") as csvfile:
        writer = csv.writer(csvfile)
        writer.writerow(["orghandle", "id", "buildType", "started_at", "completed_at", "status", "conclusion"])
        writer.writerows(rows)

    print(f"CSV file saved: {output_csv}")

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Process JMeter JTL files and extract build information.")
    parser.add_argument("jtl_file", nargs="?", default="result-tree.jtl", help="Path to the JTL file (default: result-tree.jtl)")
    parser.add_argument("--buildType", choices=["initialBuild", "lastBuild"], help="Filter by build type (optional)")
    parser.add_argument("--output", default="jmeter_summary.csv", help="Output CSV file (default: jmeter_summary.csv)")

    args = parser.parse_args()
    process_jtl_file(args.jtl_file, args.output, args.buildType)
