const proxy = require('express-http-proxy');
const app = require('express')();
const request = require('request');
app.use('/api/', proxy('http://localhost:8080', {
    proxyReqPathResolver: function (req) {
        const parts = req.url.split('?');
        console.log(parts);
        const queryString = parts[1];
        const updatedPath = "/api" + parts[0];
        return updatedPath + (queryString ? '?' + queryString : '');
    },
    proxyReqOptDecorator: function(proxyReqOpts, srcReq) {
        proxyReqOpts.headers['Host'] = srcReq.headers.host;
        return proxyReqOpts;
    }
}));
app.use('/', proxy('http://localhost:3000'));

function check(service) {
    return function (err, res, body) {
        if (err === null) {
            console.log(service + ' is reachable from proxy server')
        } else {
            console.log(service + ' is not reachable from proxy server')
        }
    };
}

let callback = () => {
    request('http://localhost:8080/api', check("backend"));
    request('http://localhost:3000', check("frontend"));
};

app.listen(8000, callback);
console.log('Proxy listening on port 8000');
