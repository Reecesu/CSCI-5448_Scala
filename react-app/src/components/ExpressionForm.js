import React, { useState } from 'react';
import { Button, TextField, Typography } from '@mui/material';

function ExpressionForm() {
  const [expression, setExpression] = useState('');
  const [result, setResult] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
  
    const response = await fetch(`${process.env.REACT_APP_BACKEND_URL}/evaluate`, {
      method: 'POST',
      mode: 'cors',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ hello: 'world'})
    //   body: JSON.stringify({ expression }),
    });
    console.log(response);
  
    const data = await response.json();
    console.log('Server response:', data); // Add this line to log the server response
    setResult(data.result);
  };

  const formStyle = {
    display: 'flex',
    alignItems: 'center',
    marginBottom: '10px',
  };

  const textFieldStyle = {
    marginRight: '10px',
  };

  return (
    <div>
      <form onSubmit={handleSubmit} style={formStyle}>
        <TextField
          label="Expression"
          value={expression}
          onChange={(e) => setExpression(e.target.value)}
          style={textFieldStyle}
        />
        <Button type="submit" variant="contained">Evaluate</Button>
      </form>
      <Typography>{result}</Typography>
    </div>
  );
}

export default ExpressionForm;
