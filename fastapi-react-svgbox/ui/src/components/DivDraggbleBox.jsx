import DivBox from "./DivBox";

/*
* Setup the draggble box
*/
const box = [
    { id: "1", width: 50, height: 50, color: "blue" },
    { id: "2", width: 50, height: 50, color: "orange" },
    { id: "3", width: 50, height: 50, color: "red" },
  ];
  
  const DraggableDivBox = ({ setBoxData }) => {
    const onDragStart = (boxData) => {
      setBoxData(boxData);
    };
  
    const onDragEnd = () => {};
    
    return (
      <div className="sidebar dragging-div-box">
        {box.map((item) => (
          <DivBox
            key={item.id}
            dragObject={item}
            onDragStart={(boxData) => onDragStart(boxData)}
            onDragEnd={() => onDragEnd()}
          >
            <div className="box" style={{ backgroundColor: item.color }}>
            </div>
          </DivBox>
        ))}
      </div>
    );
  };
  
  export default DraggableDivBox;
  
  

  