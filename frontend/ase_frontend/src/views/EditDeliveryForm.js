import Button from "@mui/material/Button";
import {TextField, Typography, Paper, Container, MenuItem, Select, InputLabel, FormControl} from "@mui/material"


const EditDeliveryFrom = () => {
    const Id = 23;
    return (
        <Container>
            <Paper  sx={{border: 1, borderRadius: 1}}>
                <Typography component="h1" variant="h6" align="center" marginBottom={5}>
                    Change Delivery No {Id}
                </Typography>
                <TextField sx={{minWidth: 120, margin: 1}} size="small" name="targetBox" label="Target Box"/>
                <br/>
                <TextField sx={{minWidth: 120, margin: 1}} size="small" name="targetCustomer" label="Target Customer"/>
                <br/>
                <TextField sx={{minWidth: 120, margin: 1}} size="small" name="responsibleDriver" label="Responsible Driver"/>
                <br/>
                <FormControl sx={{minWidth: 120, margin: 1}} size="small">
                    <InputLabel id="selectStatus">Status</InputLabel>
                    <Select name="deliveryStatus" labelId="selectStatus" label="">
                        <MenuItem value=""><em>None</em></MenuItem>
                        <MenuItem value="open">Open</MenuItem>
                        <MenuItem value="pickedUp">Picked Up</MenuItem>
                        <MenuItem value="delivered">Delivered</MenuItem>
                    </Select>
                </FormControl>
                <br/>
                <Button sx={{minWidth: 100, margin: 1}} variant="contained" size="small" color="success">Change</Button>
                <Button sx={{minWidth: 100, margin: 1}} variant="contained" size="small" color="error">Cancel</Button>
            </Paper>
        </Container>
    );
}

export default EditDeliveryFrom