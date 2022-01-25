import Button from "@mui/material/Button";
import {TextField, Typography, Paper, Container, MenuItem, Select, InputLabel, FormControl} from "@mui/material"
import {useDispatch} from "react-redux";
import React, {useState} from "react";
import {addElement} from "./boxesSlice";


const AddNewBoxForm = () => {

    const dispatch = useDispatch();

    const[elemAddress,setAddress] = useState("");
    const[elemBoxStatus,setBoxStatus] = useState("");
    const[elemDeliveryIDs,setDeliveryIDs] = useState("");
    const[elemRaspberryPiID,setRaspberryPiID] = useState("");

    const handleChangeAddress = (e) => {
        setAddress(e.target.value);
    }

    const handleChangeBoxStatus = (e) => {
        setBoxStatus(e.target.value);
    }

    const handleChangeDeliveryIDs = (e) => {
        setDeliveryIDs(e.target.value);
    }

    const handleChangeRaspberryPiID = (e) => {
        setRaspberryPiID(e.target.value);
    }

    function getElem() {
        const newElement = {
            elemAddress:  elemAddress,
            elemBoxStatus: elemBoxStatus,
            elemDeliveryIDs: elemDeliveryIDs,
            elemRaspberryPiID: elemRaspberryPiID
        }

        setAddress("");
        setBoxStatus("");
        setDeliveryIDs("");
        setRaspberryPiID("")

        return newElement
    }

    return (
        <Container>
            <Paper  sx={{border: 1, borderRadius: 1}}>
                <Typography component="h1" variant="h6" align="center" marginBottom={5}>
                    Add new Box
                </Typography>
                <TextField sx={{minWidth: 120, margin: 1}} size="small" name="address" label="Address" value={elemAddress} onChange={handleChangeAddress}/>
                <br/>
                <TextField sx={{minWidth: 120, margin: 1}} size="small" name="deliveryIds" label="Delivery IDs" value={elemDeliveryIDs} onChange={handleChangeDeliveryIDs}/>
                <br/>
                <TextField sx={{minWidth: 120, margin: 1}} size="small" name="raspberryPiID" label="RaspberryPi ID" value={elemRaspberryPiID} onChange={handleChangeRaspberryPiID}/>
                <br/>
                <FormControl sx={{minWidth: 120, margin: 1}} size="small">
                    <InputLabel id="selectStatus">Status</InputLabel>
                    <Select name="boxStatus" labelId="selectStatus" label="" value={elemBoxStatus} onChange={handleChangeBoxStatus}>
                        <MenuItem value=""><em>None</em></MenuItem>
                        <MenuItem value="available">Available</MenuItem>
                        <MenuItem value="occupied">Occupied</MenuItem>
                    </Select>
                </FormControl>
                <br/>
                <Button sx={{minWidth: 100, margin: 1}} variant="contained" size="small" color="success" onClick={()=> dispatch(addElement(getElem()))}>Add</Button>
            </Paper>
        </Container>
    );
}

export default AddNewBoxForm;