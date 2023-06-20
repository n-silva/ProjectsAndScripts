import React from "react";

/**
 * Define the DivBox to drag at SvgContainer
 */
const DivBox = ({ children, dragObject, onDragStart, onDragEnd }) => {
  /**
   * 
   * @param {children} : Div box pre-defined (3 by default)
   * @param {dragObject} : Div Box object data to be dropped in the svg canvas
   * @param {onDragStart} : Start dragging the box event action
   * @param {onDragEnd} : Stop dragging the box event action
   */
  const onDragStarting = (event) => {
    // Get the block coordinates
    let currentTargetRect = event.currentTarget.getBoundingClientRect();
    // Find the offset of the mouse from those coordinates.
    const offset = [
      event.clientX - currentTargetRect.left,
      event.clientY - currentTargetRect.top
    ];

    // Pass the drag data
    onDragStart({ dragObject, offset });
  };

  const onDragEnding = (e) => {
    e.stopPropagation();
    onDragEnd();
  };

  return (
    <div
      className="movebox"
      draggable={true}
      onDragStart={onDragStarting}
      onDragEnd={onDragEnding}
    >
      {children}
    </div>
  );
};

export default DivBox;