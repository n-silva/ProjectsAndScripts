import React, { useState, useEffect } from 'react';

import { LoadData } from './resolvers';
import DivDraggbleBox  from './components/DivDraggbleBox';
import {DeleteButton, DeleteOneButton} from './components/DelBox';
import {SvgContainer} from './components/SvgContainer';
import { AddBox } from './components/AddBox'; 

import './App.css';

function App(){
  const [boxData, setBoxData] = useState(null);
  const [isloaded, setLoaded] = useState(false);
  const data = LoadData();

  useEffect(()=>{
    if (!isloaded){
      if (data){
          var svg_active = document.querySelector('#svg-box')
          if (svg_active){
              AddBox.draw(svg_active, data);
          }
          setLoaded(true);
      } 
    }
  },[isloaded, data]);
  
  return (
    <div className='App'>
      <div className="flex-container">
        <div className="flex-child magenta parent">
          <DeleteOneButton />
          <DivDraggbleBox setBoxData={(boxData) => setBoxData(boxData)} />
          <div className='flex-container-foot absolute'>
            <span id="counter" data-box-count={0}>
              <div> 0 Elements </div>
            </span>
            <DeleteButton id={"del-all"} label={"Clear"} clear={true}/>
          </div>
        </div>
        
        <div className="flex-child green">
          <SvgContainer draggedData={boxData} />
        </div>
      </div>      
    </div>

  );
}

export default App;
