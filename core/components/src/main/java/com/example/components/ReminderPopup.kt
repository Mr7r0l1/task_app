import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderPopup(
    onDismissRequest: () -> Unit,
    onTimeSelected: (LocalTime) -> Unit
) {
    // 1. Create a state object to hold and manage the time selection
    val timePickerState = rememberTimePickerState(
        initialHour = 9,
        initialMinute = 0,
        is24Hour = false
    )

    // 2. Use the Dialog to wrap the content
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Selecciona una hora") },
        text = {
            // 3. Place the TimePicker content here
            TimePicker(state = timePickerState)
        },
        confirmButton = {
            TextButton(onClick = {
                // Extract the selected time
                val selectedTime = LocalTime.of(timePickerState.hour, timePickerState.minute)
                onTimeSelected(selectedTime)
                onDismissRequest()
            }) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancelar")
            }
        }
    )
}