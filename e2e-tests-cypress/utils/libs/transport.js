const https = require('https');

module.exports = {
    delay: function(time) {
        return new Promise(resolve => setTimeout(resolve, time));
    },

    sendGraphQLPost: async function(token, hostname, body, callback) {
        // For debugging
        //console.log(`\nsendGraphQLPost body: ${body}\n`)

        const options = {
            hostname: hostname,
            port: 443,
            path: `/projects/1.0.0/graphql`,
            method: 'POST',
            headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`,
            'Content-Length': Buffer.byteLength(body)
            },
        };

        const res = await httpsPost({body,
            options
        })

        return callback(res);
    },

};

function httpsPost({body, options}) {
    return new Promise((resolve,reject) => {
        const req = https.request(options, res => {
            const chunks = [];
            res.on('data', data => chunks.push(data))
            res.on('end', () => {
                let resBody = Buffer.concat(chunks);
                switch(res.headers['content-type']) {
                    case 'application/json':
                        resBody = JSON.parse(resBody);
                        break;
                }
                resolve(resBody)
            })
        })
        req.on('error',reject);
        if(body) {
            req.write(body);
        }
        req.end();
    })
}

