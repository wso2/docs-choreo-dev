import csv
import argparse
import datetime
import numpy as np

def compute_statistics(input_csv, output_csv):
    """Computes build statistics and writes them to an aggregate report."""
    durations = []
    error_count = 0
    total_samples = 0
    start_times = []
    end_times = []

    with open(input_csv, "r") as csvfile:
        reader = csv.DictReader(csvfile)
        for row in reader:
            started_at = row["started_at"]
            completed_at = row["completed_at"]
            status = row["conclusion"]

            if started_at and completed_at:
                start_dt = datetime.datetime.fromisoformat(started_at.replace("Z", "+00:00"))
                end_dt = datetime.datetime.fromisoformat(completed_at.replace("Z", "+00:00"))
                duration_ms = (end_dt - start_dt).total_seconds()
                durations.append(duration_ms)
                start_times.append(start_dt)
                end_times.append(end_dt)


            total_samples += 1
            if status.lower() != "success":
                error_count += 1

    if not durations:
        print("No valid build durations found.")
        return

    # Compute statistics
    avg_latency = np.mean(durations) / 60
    median_latency = np.median(durations) / 60
    percentiles = {p: np.percentile(durations, p) / 60 for p in [90, 95, 99]}
    min_latency = np.min(durations) / 60
    max_latency = np.max(durations) / 60
    error_percentage = (error_count / total_samples) * 100 if total_samples > 0 else 0

    # # Compute throughput (builds per minute)
    # start_times = sorted(durations)
    # throughput = total_samples / ((max_latency - min_latency) / (1000 * 60)) if total_samples > 1 else 0

    # Compute throughput (builds per minute)
    total_duration_seconds = (max(end_times) - min(start_times)).total_seconds() / 60
    throughput = total_samples / total_duration_seconds if total_duration_seconds > 0 else 0

    # Write statistics to CSV
    with open(output_csv, "w", newline="") as csvfile:
        writer = csv.writer(csvfile)
        writer.writerow(["Label", "# Samples", "Average", "Median", "90% Line", "95% Line", "99% Line", "Min", "Max", "Error %", "Throughput"])
        writer.writerow([
            "Build",total_samples, avg_latency, median_latency, percentiles[90], percentiles[95], percentiles[99],
            min_latency, max_latency, error_percentage, throughput
        ])

    print(f"Aggregate report saved: {output_csv}")


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Compute statistics from JMeter summary CSV.")
    parser.add_argument("--input",nargs="?", default="jmeter_summary.csv", help="Path to the summary CSV file")
    parser.add_argument("--output", default="AggregateReport.csv", help="Output CSV file for aggregate statistics")

    args = parser.parse_args()
    compute_statistics(args.input, args.output)
