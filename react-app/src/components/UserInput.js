import React, { useState } from 'react';
import { Button, TextField } from '@mui/material';


class ScalaExpr {

    constructor(index, expressions) {
        this.updateRight = false;
        this.index = index;
        this.expressions = expressions;
        return;
    }

    inc(props) {
        this.updateRight = !this.updateRight;
        if (this.updateRight) {
          this.index = this.index + 1;
          const max = this.expressions.length - 1; 
          if (max < this.index) {
              this.index = max;
              this.updateRight = !this.updateRight;
          }
          
          props.onNewExpressionChange(this.expressions[this.index]);
        }
        else {
          props.onExpressionChange(this.expressions[this.index]);
          props.onNewExpressionChange('');
          return;
        }
    }

    dec(props) {
        this.updateRight = !this.updateRight;
        if (!this.updateRight) {
          this.index = this.index - 1;
          const min = 0;
          if (min > this.index) {
              this.index = min;
              this.updateRight = !this.updateRight;
          }
        props.onExpressionChange(this.expressions[this.index]);
        }
        else {
          props.onExpressionChange(this.expressions[this.index]);
          props.onNewExpressionChange('');
          return;
        }
    }

    getExpr() {
        return this.expressions[this.index]; 
    }
}

const scalaExpr = new ScalaExpr(0, []);

function ExpressionInput(props) {
  const [userExpression, setUserExpression] = useState('');
  // const [result] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
  
    console.log(userExpression);
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
          // expression: userExpression
          expression: "1 + 2 * 3"
      })
    });

    const data = await response.json();
      // SPWI: update the json on backend and here
    console.log('Server response:', data);
    scalaExpr.expressions = data.message;
    props.onExpressionChange(scalaExpr.getExpr());

    // const jsonStr = JSON.stringify(data);
    // // Expression(userExpression=jsonStr);
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

  const handleBack = () => {
      scalaExpr.dec(props);
      return;
  };

  const handleNext = () => {
      // 1 + 2 * 3
      // 1 + 2 * 3 —> 1 + 6
      // 1 + 6
      // 1 + 6 —> 7
      // 7
      scalaExpr.inc(props);
      return;
  };

  return (
    <div>
      <form onSubmit={handleSubmit} style={formStyle}>
      <TextField
          label="Expression"
          value={userExpression}
          onChange={(e) => setUserExpression(e.target.value)}
          style={textFieldStyle}
          fullWidth
          multiline
          rows={4}
        />
        <div>
          <Button type="submit" variant="contained" style={buttonStyle}>
            Send
          </Button>
          <Button onClick={handleBack} variant="contained" style={buttonStyle}>
            ← Back
          </Button>
          <Button onClick={handleNext} variant="contained" style={buttonStyle}>
            Next →
          </Button>
        </div>
      </form>
    </div>
  );
}

export default ExpressionInput;
