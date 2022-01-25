import React, { useState } from 'react';
import Button from "@mui/material/Button";
import {TextField, Typography, Paper, Container, MenuItem, Select, InputLabel, FormControl} from "@mui/material";
import {useDispatch, useSelector} from "react-redux";
import {cancelEdit, editElement, getEditBox} from "./boxesSlice";


const EditBoxForm = () => {

    const dispatch = useDispatch();
    const elementToChange = useSelector(getEditBox);

    const[elemAddress,setAddress] = useState(elementToChange.elemAddress);
    const[elemBoxStatus,setBoxStatus] = useState(elementToChange.elemBoxStatus);
    const[elemDeliveryIDs,setDeliveryIDs] = useState(elementToChange.elemDeliveryIDs);
    const[elemRaspberryPiID,setRaspberryPiID] = useState(elementToChange.elemRaspberryPiID);


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
        return {
            elemAddress:  elemAddress,
            elemBoxStatus: elemBoxStatus,
            elemDeliveryIDs: elemDeliveryIDs,
            elemRaspberryPiID: elemRaspberryPiID
        }
    }

    return (
        <Container>
            <Paper  sx={{border: 1, borderRadius: 1}}>
                <Typography component="h1" variant="h6" align="center" marginBottom={5}>
                    Change Delivery No {elementToChange.id}
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
                <Button sx={{minWidth: 100, margin: 1}} variant="contained" size="small" color="success"  onClick={()=> dispatch(editElement(getElem()))}>Change</Button>
                <Button sx={{minWidth: 100, margin: 1}} variant="contained" size="small" color="error" onClick={()=> dispatch(cancelEdit())}>Cancel</Button>
            </Paper>
        </Container>
    );
}

export default EditBoxForm;