const express = require('express');
const app = express();
const port = 8000;

const expressLayouts = require('express-ejs-layouts');
const db = require('./config/mongoose');
const sassMiddleware = require('node-sass-middleware');

// sass middleware for compiling scss files to css
app.use(sassMiddleware({
    src: './assets/scss',
    dest: './assets/css',
    debug: true,
    outputStyle: 'extended',
    prefix: '/css'
}))
app.use(expressLayouts);

app.set('layout extractStyles', true);
app.set('layout extractScripts', true);


app.set('view engine', 'ejs');
app.set('views', './views');
app.use(express.urlencoded());
app.use(express.static('assets'));

// for routes use localhost:8000/todo-app to see the main functionality
app.use('/', require('./routes'));


app.listen(port, function(err){
    if(err){
        console.log("Error in running the server:", err);
        return;
    }

    console.log("Server is up and running on port:", port);
    return;
})