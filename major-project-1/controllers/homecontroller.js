const Todo = require('../models/todo');

// action -- > for rendering todos app main page
module.exports.home = function(req, res){
    Todo.find({}, function(err, todos){
        if(err){
            console.log("Error in fetching todos from the database", err);
            return;
        }

        return res.render('todo_page', {
            todos: todos
        })
    })
}

// action --> for creating todo and inserting in mongodb
module.exports.createTodo = function(req, res){
    Todo.create({
       description: req.body.description,
       category: req.body.category,
       date: req.body.duedate 
    }, function(err, todo){
        if(err){
            console.log("Error in creating todo", err);
            return;
        }

        return res.redirect('back');
    })
}

// action --> for deletion of todos from mongodb database
module.exports.deleteTodos = function(req, res){
    console.log(req.body.delTodos);
    Todo.deleteMany({
        "_id" : {
            $in: req.body.delTodos
        }
    }, function(err){
        if(err){
            console.log("Error in deleting documents from MongoDB");
            return;
        }
    });
    
    return res.redirect(req.get('referer'));
}