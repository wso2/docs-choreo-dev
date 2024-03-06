# Develop web applications locally with Choreo’s Managed Authentication

The Managed Authentication capability of Choreo exposes a set of BFF endpoints which can be inoked by web applications to perform authentication and authorization. These endpoints are readily available for single page web applications which are deployed on Choreo. 

However, as a web application developer, you would also need to run the application locally on your personal workstation. You’d rightly expect the authentication and authorization to work when developing locally just as it would when the application is deployed to Choreo, without having to change the source code of your application.

Local development provides the same capabilities when developing the web application locally.

## How local development works

Local development uses a proxy that sits infront of the locally running web application. This proxy is responsible for forwarding the requests to Managed Authentication BFF endpoints (`/choreo-apis/*`, `/auth/*`) to Choreo and all other requests to the locally running web application. The proxy runs on https (using a self signed certificate) as Managed Authentication relies on secure, http-only cookies. 
After running the proxy, the developer can access the web app using the proxy's URL and continue development and the behaviour of the web app (in terms of Managed Authentication functionality) would be similar to how it would be after deplying to Choreo.  


## Configure Local Development

### Prerequisites

- A SPA web application with Managed Authentication enabled.
- Application deployed/promoted to the environment that you want to enable local development in

!!!info
    - Your locally running web application will use the same Managed Authentication configurations as the environment that you are configuring local development in.

!!!note
    Local development is only allowed in non critical environments

- Npm 18.x installed  

### Basic Configuration

- Go to the Deployment tab in the left navigation.
- Click on Local development in the environment card (under authentication section). A right drwawer will open
- Turn on the toggle to enable Local Development.
- Click Apply.

### Advanced Configuration

- In the right drawer, click on Advanced Configurations.

| Parameter      |  Description                                                  |
|----------------|---------------------------------------------------------------|
| Proxy Port     | The port on which the local development proxy server will run |

- Modify the configuration.
- Click Apply.

## Develop your web app using Local Development

### Prerequisites

- Ensure that local development is enabled for the environment that you want to use local development in.
- Ensure your web application is running locally on http://localhost on some given port.

### Develop your web app locally

=== "Using Choreo built-in identity provider"

    1. Click local development on the environment card to open the right drawer.
    2. Copy the command given in step 1 on the right drawer. 
    3. Relace [APP_PORT] with the port on which your application is running locally. 
    4. Run the command in a terminal.
    5. Access the application using the URL given in step 2 on the right drawer and start/continue development.

    !!!note
        The local development proxy runs on https using a self signed cert. Your browser may warn that the certificate is not valid. Accept the risk and proceed.  


=== "Using an external identity provider"

    1. Click local development on the environment card to open the right drawer.
    2. Copy the redirect URLs given in step 1 on the right drawer
    3. Go to the settings in the OAuth application in your IdP and add the copied URLs as allowed redirect URLs list.
    4. Copy the command given in step 2 on the right drawer. 
    5. Relace [APP_PORT] with the port on which your application is running locally. 
    6. Run the command in a terminal.
    7. Access the application using the URL given in step 3 on the right drawer and start/continue development.

    !!!note
        The local development proxy runs on https using a self signed cert. Your browser may warn that the certificate is not valid. Accept the risk and proceed.  
