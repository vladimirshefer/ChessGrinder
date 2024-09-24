const proxy = require('express-http-proxy');
const app = require('express')();
const express = require('express')
const request = require('request');
app.use('/', proxy('http://localhost:3000'));
app.use('/api', proxy('http://localhost:8080/api'));

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
