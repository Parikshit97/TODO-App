const express = require('express');
const router = express.Router();

console.log('Router loaded . . .');

const homeController = require('../controllers/homecontroller.js');
router.get('/todo-app', homeController.home);
router.post('/createTodo', homeController.createTodo);
router.post('/deleteTodos', homeController.deleteTodos);

module.exports = router;