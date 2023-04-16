import React, { useState } from 'react';
import { Button, TextField } from '@mui/material';

function ExpressionInput(props) {
  const [expression, setExpression] = useState('');
  // const [result] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
  
    console.log(expression);
    const response = await fetch(`${process.env.REACT_APP_BACKEND_URL}/evaluate`, {
      method: 'POST',
      mode: 'cors',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ 
          // TODO: find out from user data
          evaluationConditions: {
              scope: "LexicalScope",
              types: "NoConversions",
              lazyEager: "EagerCondition"
          },
          expression: expression
      })
    });
    console.log(response);

    const data = await response.json();
    console.log('Server response:', data);
    props.onResultChange(data.message);

    // const jsonStr = JSON.stringify(data);
    // // Expression(expression=jsonStr);
    // setResult(data.result)
    // // App.StoreContext.store(jsonStr);
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
    </div>
  );
}

export default ExpressionInput;
