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
    });
    console.log(response);
  
    const data = await response.json();
    console.log('Server response:', data);
    setResult(data.result);
  };

  const formStyle = {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'flex-start',
    marginBottom: '10px',
  };

  const textFieldStyle = {
    marginBottom: '10px',
    marginLeft: '10px',
  };

  const buttonStyle = {
    marginLeft: '10px',
  };

  return (
    <div>
      <form onSubmit={handleSubmit} style={formStyle}>
      <TextField
          label="Expression"
          value={expression}
          onChange={(e) => setExpression(e.target.value)}
          style={textFieldStyle}
          fullWidth
          multiline
          rows={4}
        />
        <div>
          <Button type="submit" variant="contained" style={buttonStyle}>
            Send
          </Button>
          <Button type="submit" variant="contained" style={buttonStyle}>
            ← Back
          </Button>
          <Button type="submit" variant="contained" style={buttonStyle}>
            Next →
          </Button>
        </div>
      </form>
      <Typography>{result}</Typography>
    </div>
  );
}

export default ExpressionForm;
