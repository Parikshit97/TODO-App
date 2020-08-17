const mongoose = require('mongoose');

// todo schema where description, category and date are present
const todoSchema = new mongoose.Schema({
    description: {
        type: String,
        required: true
    },
    category: {
        type: String,
        required: true
    },
    date: {
        type: String,
        required: true
    }
},{
    timestamps: true
});

const Todo = mongoose.model('Todo', todoSchema);
module.exports = Todo;
