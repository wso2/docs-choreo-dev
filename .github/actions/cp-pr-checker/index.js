const core = require('@actions/core')
const github = require('@actions/github')

async function run() {
    if (github.context.payload.action == "edited") {
        const incompleteChecklist = checkForIncompleteChecklist(github.context.payload.pull_request.body);

        if (incompleteChecklist) {
            console.log(`Pending checklist items exists in the PR body.`);
            core.setFailed('There are pending checklist items. Please check them to proceed.');
            return;
        } else {
            console.log(`No pending checklist items in the body.`);
            return;
        }
    } else {
        console.log(`Ignoring event ${github.context.payload.action}`);
        return;
    }
}

function checkForIncompleteChecklist(eventIssueBody) {
    let regex1 = RegExp('- \\[ \\]')
    return regex1.test(eventIssueBody)
}

run();   
