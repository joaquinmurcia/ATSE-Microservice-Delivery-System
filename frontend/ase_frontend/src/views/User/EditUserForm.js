import React, { useState } from 'react';
import Button from "@mui/material/Button";
import {TextField, Typography, Paper, Container, MenuItem, Select, InputLabel, FormControl} from "@mui/material";
import {useDispatch, useSelector} from "react-redux";
import {cancelEdit, editElement, getEditUser} from "./usersSlice";


const EditUserFrom = () => {

    const dispatch = useDispatch();
    const elementToChange = useSelector(getEditUser);

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
                <TextField sx={{minWidth: 120, margin: 1}} size="small" name="name" label="Name" value={elemName} onChange={handleChangeName}/>
                <br/>
                <TextField sx={{minWidth: 120, margin: 1}} size="small" name="password" label="Password" value={elemPassword} onChange={handleChangePassword}/>
                <br/>
                <FormControl sx={{minWidth: 120, margin: 1}} size="small">
                    <InputLabel id="selectRole">Status</InputLabel>
                    <Select name="role" labelId="selectRole" label="" value={elemRole} onChange={handleChangeRole}>
                        <MenuItem value=""><em>None</em></MenuItem>
                        <MenuItem value="customer">Customer</MenuItem>
                        <MenuItem value="deliverer">Deliverer</MenuItem>
                        <MenuItem value="dispatcher">Dispatcher</MenuItem>
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