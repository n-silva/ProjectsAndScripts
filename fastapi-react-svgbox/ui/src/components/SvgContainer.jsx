import React, { useRef, useEffect, useState } from "react";
import { useMutation} from "@apollo/client";
import { CREATE_BOX } from "../resolvers";
import {AddBox} from "./AddBox";

/*
 * Count the box within the canvas
 */
const Counter = (num=1) => {
  var span = document.querySelector('#counter');
  if (span){
      let count = Number(span.getAttribute("data-box-count"));
      count += num;
      if (count < 0 || num===0) count = 0;
      span.setAttribute("data-box-count", Number(count));
      span.innerHTML = "<div>"+count+" Elements </div>";
  }
}

/**
 * Convert DOM coordinates to SVG coordinates based on SVG offset and zoom level
 */
const convertCoordinatesDOMtoSVG = (svg, x, y) => {
  const svgPoint = svg.createSVGPoint();
  svgPoint.x = x;
  svgPoint.y = y;
  return svgPoint.matrixTransform( svg.getScreenCTM().inverse() );
};

const SvgContainer = ({ draggedData }) => {
  const svg_active = useRef(null);
  const [isSave, setSave] = useState(false);
  const [saveData, setSaveData] = useState(null);
  const [insertBox] = useMutation(CREATE_BOX);
  
  useEffect(() => {
    if (isSave){
        insertBox({ variables: { 
          id: String(saveData?.id),
          color: saveData?.color,
          sizeX: saveData?.width,
          sizeY: saveData?.height,
          posX: saveData?.x,
          posY: saveData?.y
       } })
       setSave(false);
       setSaveData(null);
    }
  }, [setSave,saveData, setSaveData, isSave, insertBox]);
 
  
  const onDragOver = (e) => {
    e.preventDefault();
    svg_active.current.setAttribute("class", "drag-over");
  };

  const onDragLeave = () => {
    svg_active.current.classList.remove("drag-over");
  };

    
  const onDrop = (e) => {
    e.stopPropagation();
    svg_active.current.classList.remove('drag-over');
    const { x, y } = convertCoordinatesDOMtoSVG(
      svg_active.current,
      e.clientX - draggedData.offset[0],
      e.clientY - draggedData.offset[1]
    );

    
    const node_id = Math.floor(Math.random() * (100 - 1 + 1)) + 1;
    const box_size = Math.floor(Math.random() * (100 - 30 + 30)) + 30;

    // Add the node to the list of nodes.
    const node = {
      id: node_id,
      color: draggedData.dragObject.color,
      width: box_size,
      height: box_size,
      x,
      y
    }

    const svg_box = document.querySelector('#svg-box');
    // Add the node
    AddBox.add_node(svg_box, node);
    setSave(true);
    setSaveData(node);
    return false;
  };
    
  return (
    <div
      className="svgContainer"
      onDrop={(e) => onDrop(e)}
      onDragLeave={(e) => onDragLeave(e)}
      onDragOver={(e) => onDragOver(e)}
    >
      <svg ref={svg_active} id="svg-box">
      </svg>
    </div>
  );
};

export {Counter, SvgContainer};
