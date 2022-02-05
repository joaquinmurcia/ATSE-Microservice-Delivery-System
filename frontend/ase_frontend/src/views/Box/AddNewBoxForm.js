import Button from "@mui/material/Button";
import {TextField, Typography, Paper, Container, MenuItem, Select, InputLabel, FormControl} from "@mui/material"
import {useDispatch} from "react-redux";
import React, {useState} from "react";
import {addBoxAsync} from "./boxesSlice";


const AddNewBoxForm = () => {

    const dispatch = useDispatch();

    //Adress
    const[elemStreetName,setStreetName] = useState("");
    const[elemStreetNumber,setStreetNumber] = useState("");
    const[elemPostcode,setPostcode] = useState("");
    const[elemCity,setCity] = useState("");
    const[elemCountry,setCountry] = useState("");

    const[elemBoxStatus,setBoxStatus] = useState("");
    const[elemDeliveryIDs,setDeliveryIDs] = useState("");
    const[elemRaspberryPiID,setRaspberryPiID] = useState("");

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

    const handleChangeDeliveryIDs = (e) => {
        setDeliveryIDs(e.target.value);
    }

    const handleChangeRaspberryPiID = (e) => {
        setRaspberryPiID(e.target.value);
    }

    function getElem() {
        const newElement = {
            address: {
                streetName: elemStreetName,
                streetNumber: elemStreetNumber,
                postcode: elemPostcode,
                city: elemCity,
                country: elemCountry
            },
            boxStatus: elemBoxStatus,
            deliveryIDs: elemDeliveryIDs,
            raspberryPiID: elemRaspberryPiID
        };

        setStreetName("");
        setStreetNumber("");
        setPostcode("");
        setCity("");
        setCountry("");

        setBoxStatus("");
        setDeliveryIDs("");
        setRaspberryPiID("");

        return newElement;
    }

    return (
        <Container>
            <Paper  sx={{border: 1, borderRadius: 1}}>
                <Typography component="h1" variant="h6" align="center" marginBottom={5}>
                    Add new Box
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
                <Button sx={{minWidth: 100, margin: 1}} variant="contained" size="small" color="success" onClick={()=> dispatch(addBoxAsync(getElem()))}>Add</Button>
            </Paper>
        </Container>
    );
}

export default AddNewBoxForm;