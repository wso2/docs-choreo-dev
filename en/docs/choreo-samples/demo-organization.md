# Demo Organization

The Demo Organization in Choreo allows you to explore a fully deployed system in a read-only mode. Choreo maintains this organization and provides access to real-world use cases, helping you understand how Choreo can manage complex applications.

## Prerequisites

- Sign in to Choreo.  
- If you're signing in for the first time, create an organization:  
    1. Go to the [Choreo Console](https://console.choreo.dev/) and sign in using Google, GitHub, or Microsoft.  
    2. Enter a unique organization name, for example, `Stark Industries`.  
    3. Accept the privacy policy and terms of use.  
    4. Click **Create** to set up the organization.

## How to Join the Demo Organization

1. After signing in, you will see your organization name in the top left corner.  
2. Click the dropdown next to your organization name.  
3. Under **Invited Organizations**, you will see the **Demo Organization** with a **Join** button next to it.  
4. Click **Join** to access the Demo Organization.

## System Overview

The system deployed in the Demo Organization is a **Customer Reward Management System**. Users can redeem rewards, and a workflow retrieves user details from the JWT token, forwards them to the external vendor via the **Vendor Management API**, and stores the reward confirmation in the user's profile for future discounts.

You can find further details and the source code of the deployed system [here](https://github.com/wso2/choreo-samples/tree/main/customer-reward-management#readme).
