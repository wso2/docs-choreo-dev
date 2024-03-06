import { createObjectCsvWriter } from 'csv-writer';

export class CSVWriter {
  private username: string;

  constructor(username: string) {
    this.username = username;
    this.createUserCsv();
  }
  
  public createUserCsv(): void {
    const csvFilePath = `./cypress/${this.username}_intercepted_results.csv`;
    cy.writeFile(csvFilePath, '');
  }


  // Function to write interception results to CSV
  public writeInterceptionResultsToCsv(results: any): void {
    const csvWriter = createObjectCsvWriter({
      path: `./cypress/${this.username}_intercepted_results.csv`,
      header: [
        { id: 'requestBody', title: 'Request Body' },
        { id: 'responseBody', title: 'Response Body' }
      ]
    });

    csvWriter.writeRecords([results])
      .then(() => console.log(`Interception results written to ${this.username}_intercepted_results.csv`))
      .catch((error: any) => console.error('Error writing interception results to CSV:', error));
  }
}


