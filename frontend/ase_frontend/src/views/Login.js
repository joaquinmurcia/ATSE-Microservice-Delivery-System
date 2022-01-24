import React, { useState } from 'react';
import Button from "@mui/material/Button";
import {TextField, Typography, Paper, Container} from "@mui/material";

function Login(){

    const[userName,setUserName] = useState("");
    const[password,setPassword] = useState("");

    const handleUserName = (e) => {
        setUserName(e.target.value);
    }

    const handlePassword = (e) => {
        setPassword(e.target.value);
    }

    function loginRequest() {
        const temp = window.btoa(userName + ":" + password);
        console.log(temp);
        const requestOptions = {
            method: "POST",
            headers: {
              Authorization: "Basic " + temp
            }
        }
        const response = fetch('http://localhost:9000/usermanagement/auth', requestOptions).then(handleResponse);
        return response

    }

    function handleResponse(response) {
        console.log(response);
        return response.text().then(text => {
            const data = text && JSON.parse(text);
            if (!response.ok) {
                if (response.status === 401) {
                    // auto logout if 401 response returned from api
                    //logout();
                    //                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  location.reload(true);
                }

                const error = (data && data.message) || response.statusText;
                return Promise.reject(error);
            }

            return data;
        });
    }
    
    return (
        <Container>
            <Paper  sx={{border: 1, borderRadius: 1}}>
                <Typography component="h1" variant="h6" align="center" marginBottom={5}>
                    Login
                </Typography>
                <TextField sx={{minWidth: 120, margin: 1}} size="small" name="userName" label="UserName" value={userName} onChange={handleUserName}/>
                <br/>
                <TextField sx={{minWidth: 120, margin: 1}} size="small" name="password" label="Password" value={password} onChange={handlePassword}/>
                <br/>
                <Button sx={{minWidth: 100, margin: 1}} variant="contained" size="small" onClick={()=> loginRequest()}>Login</Button>
            </Paper>
        </Container>);
}
export default Login;