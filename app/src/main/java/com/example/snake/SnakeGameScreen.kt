package com.example.snake

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.random.Random

data class Position(val x: Int, val y: Int)

enum class Direction { UP, DOWN, LEFT, RIGHT }

@Composable
fun SnakeGameScreen() {
    val boardSize = 20
    val cellSize = 20.dp
    var snake by remember { mutableStateOf(listOf(Position(5, 5))) }
    var direction by remember { mutableStateOf(Direction.RIGHT) }
    var food by remember { mutableStateOf(generateFood(boardSize, snake)) }
    var score by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(150)
            val newHead = getNextPosition(snake.first(), direction)
            if (newHead in snake || !isValidPosition(newHead, boardSize)) {
                snake = listOf(Position(5, 5))
                direction = Direction.RIGHT
                score = 0
                food = generateFood(boardSize, snake)
            } else {
                snake = listOf(newHead) + snake.dropLast(1)
                if (newHead == food) {
                    snake = snake + snake.last()
                    score += 10
                    food = generateFood(boardSize, snake)
                }
            }
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(Color.Black),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text("Score: $score", color = Color.White, style = MaterialTheme.typography.headlineSmall)

        Canvas(
            modifier = Modifier
                .size((boardSize * cellSize.value).dp)
                .padding(16.dp)
        ) {
            snake.forEach {
                drawRect(Color.Green, topLeft = androidx.compose.ui.geometry.Offset(it.x * cellSize.toPx(), it.y * cellSize.toPx()), size = androidx.compose.ui.geometry.Size(cellSize.toPx(), cellSize.toPx()))
            }
            drawRect(Color.Red, topLeft = androidx.compose.ui.geometry.Offset(food.x * cellSize.toPx(), food.y * cellSize.toPx()), size = androidx.compose.ui.geometry.Size(cellSize.toPx(), cellSize.toPx()))
        }

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { direction = Direction.LEFT }) { Text("Left") }
            Button(onClick = { direction = Direction.UP }) { Text("Up") }
            Button(onClick = { direction = Direction.DOWN }) { Text("Down") }
            Button(onClick = { direction = Direction.RIGHT }) { Text("Right") }
        }
    }
}

fun getNextPosition(head: Position, direction: Direction): Position {
    return when (direction) {
        Direction.UP -> Position(head.x, head.y - 1)
        Direction.DOWN -> Position(head.x, head.y + 1)
        Direction.LEFT -> Position(head.x - 1, head.y)
        Direction.RIGHT -> Position(head.x + 1, head.y)
    }
}

fun isValidPosition(pos: Position, boardSize: Int): Boolean {
    return pos.x in 0 until boardSize && pos.y in 0 until boardSize
}

fun generateFood(boardSize: Int, snake: List<Position>): Position {
    var newFood: Position
    do {
        newFood = Position(Random.nextInt(boardSize), Random.nextInt(boardSize))
    } while (newFood in snake)
    return newFood
}
