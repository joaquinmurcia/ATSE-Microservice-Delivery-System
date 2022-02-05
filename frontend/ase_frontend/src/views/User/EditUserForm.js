import React, { useState } from 'react';
import Button from "@mui/material/Button";
import {TextField, Typography, Paper, Container, MenuItem, Select, InputLabel, FormControl} from "@mui/material";
import {useDispatch, useSelector} from "react-redux";
import {cancelEdit, editUserAsync, getEditUser} from "./usersSlice";


const EditUserFrom = () => {

    const dispatch = useDispatch();
    const elementToChange = useSelector(getEditUser);

    const[elemName,setName] = useState(elementToChange.name);
    const[elemPassword,setPassword] = useState("");
    const[elemRFIDToken,setRFIDToken] = useState(elementToChange.rfidToken);
    const[elemEmail,setEmail] = useState(elementToChange.email);
    const[elemRole,setRole] = useState(elementToChange.role);

    const handleChangeName = (e) => {
        setName(e.target.value);
    }

    const handleChangePassword = (e) => {
        setPassword(e.target.value);
    }

    const handleChangeRFIDToken = (e) => {
        setRFIDToken(e.target.value);
    }

    const handleChangeEmail = (e) => {
        setEmail(e.target.value);
    }

    const handleChangeRole = (e) => {
        setRole(e.target.value);
    }

    function getElem() {
        return {
            id: elementToChange.id,
            name:  elemName,
            password: elemPassword,
            rfidToken: elemRFIDToken,
            email: elemEmail,
            role: elemRole
        };
    }

    return (
        <Container>
            <Paper  sx={{border: 1, borderRadius: 1}}>
                <Typography component="h1" variant="h6" align="center" marginBottom={5}>
                    Change Delivery No {elementToChange.id}
                </Typography>
                <TextField sx={{minWidth: 120, margin: 1}} size="small" name="name" label="Name" value={elemName} onChange={handleChangeName}/>
                <br/>
                <TextField sx={{minWidth: 120, margin: 1}} size="small" name="password" label="New Password" value={elemPassword} onChange={handleChangePassword}/>
                <br/>
                <TextField sx={{minWidth: 120, margin: 1}} size="small" name="rfidToken" label="RFID Token" value={elemRFIDToken} onChange={handleChangeRFIDToken}/>
                <br/>
                <TextField sx={{minWidth: 120, margin: 1}} size="small" name="email" label="Email" value={elemEmail} onChange={handleChangeEmail}/>
                <br/>
                <FormControl sx={{minWidth: 120, margin: 1}} size="small">
                    <InputLabel id="selectRole">Role</InputLabel>
                    <Select name="role" labelId="selectRole" label="" value={elemRole} onChange={handleChangeRole}>
                        <MenuItem value=""><em>None</em></MenuItem>
                        <MenuItem value="ROLE_CUSTOMER">Customer</MenuItem>
                        <MenuItem value="ROLE_DELIVERER">Deliverer</MenuItem>
                        <MenuItem value="ROLE_DISPATCHER">Dispatcher</MenuItem>
                    </Select>
                </FormControl>
                <br/>
                <Button sx={{minWidth: 100, margin: 1}} variant="contained" size="small" color="success"  onClick={()=> dispatch(editUserAsync(getElem()))}>Change</Button>
                <Button sx={{minWidth: 100, margin: 1}} variant="contained" size="small" color="error" onClick={()=> dispatch(cancelEdit())}>Cancel</Button>
            </Paper>
        </Container>
    );
}

export default EditUserFrom;