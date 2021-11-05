export class GCTwillioIntegration {


    static cloneEdit() {
        cy.get('#gcalendar-to-twilio').trigger('mouseover').within(() => {
            cy.contains('Clone & Edit').click({ force: true });
        });
    }



    static configureSettings(email: string, twilioSID: string, twilioAuth: string, senderNumber: string, recipientNumber: string) {
        cy.get('[data-testid="settings-btn"]').should('be.visible').click();
        cy.wait(5000)
        cy.get('[data-testid="google-calendar-connect-btn"] button').click({ force: true });
        cy.contains('Choose connection').should('be.visible')
        if (email == "" && email == null) {
            cy.get('ul[role="menu"]>li>div>div:nth-child(2)').invoke('text').then(val => {
                this.enterEmail(val)
            })
        } else {
            this.enterEmail(email)
        }
        this.fillTwilioDetails(twilioSID, twilioAuth, senderNumber, recipientNumber)
        cy.get('[data-testid="config-save-btn"]').should('be.enabled').click();
        cy.get('#test-run-btn').should('be.enabled')
    }
    private static fillTwilioDetails(twilioSID: string, twilioAuth: string, senderNumber: string, recipientNumber: string) {
        cy.get('[placeholder="Twilio Account SID"]').type(twilioSID);
        cy.get('[placeholder="Twilio Auth Token"]').type(twilioAuth);
        cy.get('div>input[placeholder*="SMS Sender"]').type(senderNumber);
        cy.get('div>input[placeholder*="SMS Recipient"]').type(recipientNumber);
    }

    private static enterEmail(email: string) {
        cy.contains(email).click()
        cy.get('#combo-box-demo').type(email + '{downArrow}{enter}')
    }

}