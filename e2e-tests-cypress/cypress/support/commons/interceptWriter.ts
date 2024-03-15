interface FileData {
  interceptorName: string;
  requests: { url: string; method: string };
  responses: { statusCode: number; message?: string };
}

export class InterceptWriter {
  private fileData: FileData = {
    requests: {
      url: "",
      method: ""
    },
    responses: {
      statusCode: 0,
      message: ""
    },
    interceptorName: "",
  };

  public interceptAndWriteToFixture(
    interceptorName: string,
    filePath: string,
    request: any,
    response: any
  ): void {
    const requestData = {
      url: request?.url,
      method: request?.method,
    };
    const responseData = {
      statusCode: response?.statusCode,
      message: response?.statusMessage || "No message printed from test",
      body: response?.body?.data,
    };
    this.fileData.interceptorName = interceptorName;
    this.fileData.requests=requestData;
    this.fileData.responses=responseData;

    cy.log(`Request: ${JSON.stringify(requestData)}`);
    cy.log(`Response: ${JSON.stringify(responseData)}`);
    cy.writeFile(filePath, this.fileData, { flag: "a+" });
  }
}
