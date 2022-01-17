import Button from "@mui/material/Button";
import {TextField, Typography, Paper, Container, MenuItem, Select, InputLabel, FormControl} from "@mui/material"
import {useDispatch} from "react-redux";
import React, {useState} from "react";
import {addElement} from "./deliveriesSlice";


const AddNewDeliveryForm = () => {

    const dispatch = useDispatch();

    const[elemTargetBox,setTargetBox] = useState("");
    const[elemTargetCustomer,setTargetCustomer] = useState("");
    const[elemResponsibleDriver,setResponsibleDriver] = useState("");
    const[elemDeliveryStatus,setDeliveryStatus] = useState("");

    const handleChangeTargetBox = (e) => {
        setTargetBox(e.target.value);
    }

    const handleChangeTargetCustomer = (e) => {
        setTargetCustomer(e.target.value);
    }

    const handleChangeResponsibleDriver = (e) => {
        setResponsibleDriver(e.target.value);
    }

    const handleChangeDeliveryStatus = (e) => {
        setDeliveryStatus(e.target.value);
    }

    function getElem() {
        const newElement = {
            targetBox:  elemTargetBox,
            targetCustomer: elemTargetCustomer,
            responsibleDriver: elemResponsibleDriver,
            deliveryStatus: elemDeliveryStatus
        }

        setTargetBox("");
        setTargetCustomer("");
        setResponsibleDriver("");
        setDeliveryStatus("")

        return newElement
    }

    return (
        <Container>
            <Paper  sx={{border: 1, borderRadius: 1}}>
                <Typography component="h1" variant="h6" align="center" marginBottom={5}>
                    Add new Delivery
                </Typography>
                <TextField sx={{minWidth: 120, margin: 1}} size="small" name="targetBox" label="Target Box" value={elemTargetBox} onChange={handleChangeTargetBox}/>
                <br/>
                <TextField sx={{minWidth: 120, margin: 1}} size="small" name="targetCustomer" label="Target Customer" value={elemTargetCustomer} onChange={handleChangeTargetCustomer}/>
                <br/>
                <TextField sx={{minWidth: 120, margin: 1}} size="small" name="responsibleDriver" label="Responsible Driver" value={elemResponsibleDriver} onChange={handleChangeResponsibleDriver}/>
                <br/>
                <FormControl sx={{minWidth: 120, margin: 1}} size="small">
                    <InputLabel id="selectStatus">Status</InputLabel>
                    <Select name="deliveryStatus" labelId="selectStatus" label="" value={elemDeliveryStatus} onChange={handleChangeDeliveryStatus}>
                        <MenuItem value=""><em>None</em></MenuItem>
                        <MenuItem value="open">Open</MenuItem>
                        <MenuItem value="pickedUp">Picked Up</MenuItem>
                        <MenuItem value="delivered">Delivered</MenuItem>
                    </Select>
                </FormControl>
                <br/>
                <Button sx={{minWidth: 100, margin: 1}} variant="contained" size="small" color="success" onClick={()=> dispatch(addElement(getElem()))}>Add</Button>
            </Paper>
        </Container>
    );
}

export default AddNewDeliveryForm;