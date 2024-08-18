import concurrent
import sys
import time
import requests
from concurrent.futures import ThreadPoolExecutor, as_completed

from bs4 import BeautifulSoup

# List of endpoints
endpoints = [
    # public --> service(keda)(no-customdomain)
    "https://260011ca-f51c-4e90-a64c-170ff85d354d-dev.e1-eu-north-azure.choreoapis.dev/euprojectinvoker1/greeting-service/v1.0/greeter/greet",
    # public --> service(keda)(customdomain)
    "https://eu-project-invoker-1.choreoapis.dev/euprojectinvoker1/greeting-service/v1.0/greeter/greet",
    # public --> webapp(keda)(no-customdomain)
    "https://d23fa96f-0ca0-456f-bca2-fa5f8d8d2539.e1-eu-north-azure.choreoapps.dev",
    # public --> webapp(keda)(customdomain)
    "https://eu-project-invoker-1.choreoapps.dev/",
    # public --> proxy(no-customdomain)
    "https://260011ca-f51c-4e90-a64c-170ff85d354d-dev.e1-eu-north-azure.choreoapis.dev/euprojectinvoker1/proxy/v1.0",
    # public --> proxy(customdomain)
    "https://eu-project-invoker-1.choreoapis.dev/testcustomurl",
    # public --> service --> organizational service
    "https://260011ca-f51c-4e90-a64c-170ff85d354d-dev.e1-eu-north-azure.choreoapis.dev/euprojectinvoker2/cross-project-invoker/v1.0/internalgreeting?name=jj"
    # public --> webapp(managed_auth)(keda)
    "https://ceb2e58d-b6bf-4eab-8b73-e4386877c979.e1-us-east-azure.choreoapps.dev/choreo-apis/usprojectinvoker1/reading-list-service/v1/books",
    # public --> webapp(managed auth)(no keda)
    "https://7f65027d-a04e-4f10-a39b-8f5dd4f8301d.e1-us-east-azure.choreoapps.dev/choreo-apis/usprojectinvoker1/reading-list-service-no-k/v1/books"
]

managed_auth_username = sys.argv[0]
managed_auth_password = sys.argv[1]

def fetch_status(url):
    try:
        response = requests.get(url)
        return url, response.status_code
    except requests.RequestException as e:
        return url, str(e)


def check_endpoints(endpoint_list):
    with ThreadPoolExecutor(max_workers=len(endpoint_list)) as executor:
        future_to_url = {executor.submit(fetch_status, url): url for url in endpoint_list}
        for future in as_completed(future_to_url):
            url = future_to_url[future]
            try:
                url, status = future.result()
                if status == 200:
                    print(f"Endpoint {url} is up and returned 200 OK.\n")
                else:
                    print(f"Endpoint {url} returned status code {status}.\n")
            except Exception as e:
                print(f"Endpoint {url} generated an exception: {e}\n")


def invoke_managed_auth_webapp_with_backend_service(webapp_url, sts_url, connection_service_url, service_path, username,
                                                    password):
    login_url = webapp_url + "/auth/login"
    target_url = webapp_url + connection_service_url + service_path

    # Create a session object
    session = requests.Session()

    # Step 1: Send a GET request to the login page
    response = session.get(login_url)
    soup = BeautifulSoup(response.text, 'html.parser')

    # Step 2: Parse the login page to extract form data
    form = soup.find('form')
    if not form:
        print("Login form not found")
    else:
        action = form['action']
        form_data = {input_tag['name']: input_tag.get('value', '') for input_tag in form.find_all('input')}
        form_data['username'] = username
        form_data['password'] = password

        submit_url = sts_url + action
        login_response = session.post(submit_url, data=form_data)

        if login_response.status_code == 200:
            print("Login successful")

            # Access the desired page using the authenticated session
            target_response = session.get(target_url)

            if target_response.status_code == 200:
                print("Accessed target page successfully")
                # Process the target page content
                print(target_response.text)
            else:
                print("Failed to access target page: " + str(target_response.status_code))
        else:
            print("Login failed")


if __name__ == "__main__":
    while True:
        check_endpoints(endpoints)
        with concurrent.futures.ThreadPoolExecutor() as executor:
            future1 = executor.submit(invoke_managed_auth_webapp_with_backend_service,
                                      "https://7f65027d-a04e-4f10-a39b-8f5dd4f8301d.e1-us-east-azure.choreoapps.dev",
                                      "https://260011ca-f51c-4e90-a64c-170ff85d354d-dev.e1-us-east-azure.choreosts.dev",
                                      "/choreo-apis/usprojectinvoker1/reading-list-service-no-k/v1",
                                      "/books",
                                      managed_auth_username,
                                      managed_auth_password)
            future2 = executor.submit(invoke_managed_auth_webapp_with_backend_service,
                                      "https://ceb2e58d-b6bf-4eab-8b73-e4386877c979.e1-us-east-azure.choreoapps.dev",
                                      "https://260011ca-f51c-4e90-a64c-170ff85d354d-dev.e1-us-east-azure.choreosts.dev/",
                                      "/choreo-apis/usprojectinvoker1/reading-list-service/v1",
                                      "/books",
                                      managed_auth_username,
                                      managed_auth_password)
        time.sleep(1)
