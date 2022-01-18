import React, { useState } from 'react';
import Button from "@mui/material/Button";
import {TextField, Typography, Paper, Container, MenuItem, Select, InputLabel, FormControl} from "@mui/material";
import {useDispatch, useSelector} from "react-redux";
import {cancelEdit, editElement, getEditUser} from "./usersSlice";


const EditUserFrom = () => {

    const dispatch = useDispatch();
    const elementToChange = useSelector(getEditUser);

    /*
    { id: 'id', label: 'Id', minWidth: 30},
        { id: 'name', label: 'Name', minWidth: 80 },
        { id: 'password', label: 'Password', minWidth: 80},
        { id: 'role', label: 'Role', minWidth: 80 },
        { id: 'buttons', label: 'Actions', minWidth: 90 },
     */

    const[elemName,setName] = useState(elementToChange.name);
    const[elemPassword,setPassword] = useState(elementToChange.password);
    const[elemRole,setRole] = useState(elementToChange.role);

    const handleChangeName = (e) => {
        setName(e.target.value);
    }

    const handleChangePassword = (e) => {
        setPassword(e.target.value);
    }

    const handleChangeRole = (e) => {
        setRole(e.target.value);
    }

    function getElem() {
        return {
            name:  elemName,
            password: elemPassword,
            role: elemRole
        }
    }

    return (
        <Container>
            <Paper  sx={{border: 1, borderRadius: 1}}>
                <Typography component="h1" variant="h6" align="center" marginBottom={5}>
                    Change Delivery No {elementToChange.id}
                </Typography>
                <TextField sx={{minWidth: 120, margin: 1}} size="small" name="targetBox" label="Target Box" value={elemName} onChange={handleChangeName}/>
                <br/>
                <TextField sx={{minWidth: 120, margin: 1}} size="small" name="targetCustomer" label="Target Customer" value={elemPassword} onChange={handleChangePassword}/>
                <br/>
                <FormControl sx={{minWidth: 120, margin: 1}} size="small">
                    <InputLabel id="selectStatus">Status</InputLabel>
                    <Select name="deliveryStatus" labelId="selectStatus" label="" value={elemRole} onChange={handleChangeRole}>
                        <MenuItem value=""><em>None</em></MenuItem>
                        <MenuItem value="open">Open</MenuItem>
                        <MenuItem value="pickedUp">Picked Up</MenuItem>
                        <MenuItem value="delivered">Delivered</MenuItem>
                    </Select>
                </FormControl>
                <br/>
                <Button sx={{minWidth: 100, margin: 1}} variant="contained" size="small" color="success"  onClick={()=> dispatch(editElement(getElem()))}>Change</Button>
                <Button sx={{minWidth: 100, margin: 1}} variant="contained" size="small" color="error" onClick={()=> dispatch(cancelEdit())}>Cancel</Button>
            </Paper>
        </Container>
    );
}

export default EditUserFrom