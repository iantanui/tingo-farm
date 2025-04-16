package com.example.tingofarm.ui.produce

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tingofarm.data.model.Produce

@Composable
fun ProduceCard(
    produce: Produce,
    onEdit: (Produce) -> Unit,
    onDelete: (Produce) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { },
        border = BorderStroke(1.dp, Color.LightGray),
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {

        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {

                Text(text = produce.cow, fontSize = 16.sp, fontWeight = FontWeight.Bold)

                Text(text = produce.employee, fontSize = 14.sp, fontWeight = FontWeight.Normal)
            }

            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(
                    modifier = Modifier.width(80.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Morn ${produce.morningQty} L",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal
                    )

                    Text(
                        text = "Evng ${produce.eveningQty} L",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }

            Column(modifier = Modifier.size(24.dp)) {

                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(Color.White)
                ) {
                    DropdownMenuItem(
                        text = { Text("Edit") },
                        onClick = {
                            expanded = false
                            onEdit(produce)
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Icon")
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete") },
                        onClick = {
                            expanded = false
                            onDelete(produce) // Call the onDelete function when Delete is selected
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Icon")
                        }
                    )

                }
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun ProduceCardPreview() {
    ProduceCard(
        produce = Produce(
            cow = "Nice",
            employee = "Ian",
            morningQty = 19.5,
            eveningQty = 17.0,
            date = ""
        ),
        onEdit = {},
        onDelete = {}
    )
}
