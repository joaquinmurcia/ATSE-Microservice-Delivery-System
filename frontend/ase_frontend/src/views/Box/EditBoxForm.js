import React, { useState } from 'react';
import Button from "@mui/material/Button";
import {TextField, Typography, Paper, Container, MenuItem, Select, InputLabel, FormControl} from "@mui/material";
import {useDispatch, useSelector} from "react-redux";
import {cancelEdit, editBoxAsync,getEditBox} from "./boxesSlice";
import {getCookie, parseJwt} from "../tokenReader";


const EditBoxForm = () => {

    const dispatch = useDispatch();
    const elementToChange = useSelector(getEditBox);

    const[elemStreetName,setStreetName] = useState(elementToChange.address.streetName);
    const[elemStreetNumber,setStreetNumber] = useState(elementToChange.address.streetNumber);
    const[elemPostcode,setPostcode] = useState(elementToChange.address.postcode);
    const[elemCity,setCity] = useState(elementToChange.address.city);
    const[elemCountry,setCountry] = useState(elementToChange.address.country);
    const[elemRaspberryPiID,setRaspberryPiID] = useState(elementToChange.raspberryPiID);
    const[elemBoxStatus,setBoxStatus] = useState(elementToChange.boxStatus);


    //adress
    const handleChangeStreetName = (e) => {
        setStreetName(e.target.value);
    }

    const handleChangeStreetNumber = (e) => {
        setStreetNumber(e.target.value);
    }

    const handleChangePostcode = (e) => {
        setPostcode(e.target.value);
    }

    const handleChangeCity = (e) => {
        setCity(e.target.value);
    }

    const handleChangeCountry = (e) => {
        setCountry(e.target.value);
    }

    const handleChangeBoxStatus = (e) => {
        setBoxStatus(e.target.value);
    }

    const handleChangeRaspberryPiID = (e) => {
        setRaspberryPiID(e.target.value);
    }

    function getElem() {
        return {
            id: elementToChange.id,
            address: {
                streetName: elemStreetName,
                streetNumber: elemStreetNumber,
                postcode: elemPostcode,
                city: elemCity,
                country: elemCountry
            },
            boxStatus: elemBoxStatus,
            deliveryIDs: elementToChange.deliveryIDs,
            raspberryPiID: elemRaspberryPiID
        };
    }

    return (
        <Container>
            <Paper  sx={{border: 1, borderRadius: 1}}>

                    <Typography component="h1" variant="h6" align="center" marginBottom={5}>
                        Change Delivery No {elementToChange.id}
                    </Typography>
                    <Typography component="h4" variant="h6">Adress</Typography>
                    <TextField sx={{minWidth: 120, margin: 1}} size="small" name="streetName" label="StreetName" value={elemStreetName} onChange={handleChangeStreetName}/>
                    <br/>
                    <TextField sx={{minWidth: 120, margin: 1}} size="small" name="streetNumber" label="StreetNumber" value={elemStreetNumber} onChange={handleChangeStreetNumber}/>
                    <br/>
                    <TextField sx={{minWidth: 120, margin: 1}} size="small" name="postcode" label="Postcode" value={elemPostcode} onChange={handleChangePostcode}/>
                    <br/>
                    <TextField sx={{minWidth: 120, margin: 1}} size="small" name="city" label="City" value={elemCity} onChange={handleChangeCity}/>
                    <br/>
                    <TextField sx={{minWidth: 120, margin: 1}} size="small" name="country" label="Country" value={elemCountry} onChange={handleChangeCountry}/>
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
                <Button sx={{minWidth: 100, margin: 1}} variant="contained" size="small" color="success"  onClick={()=> dispatch(editBoxAsync(getElem()))}>Change</Button>
                <Button sx={{minWidth: 100, margin: 1}} variant="contained" size="small" color="error" onClick={()=> dispatch(cancelEdit())}>Cancel</Button>
            </Paper>
        </Container>
    );
}

export default EditBoxForm;